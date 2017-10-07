package Api.Endpoints

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}
import javax.inject.Inject

import Api.ApiRequest
import CbiUtil.{CascadeSort, JsonUtil, Profiler}
import Entities._
import Services.{CacheBroker, PersistenceBroker}
import play.api.inject.ApplicationLifecycle
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class ApClassInstances @Inject() (lifecycle: ApplicationLifecycle, cb: CacheBroker, pb: PersistenceBroker)(implicit exec: ExecutionContext) extends Controller {
  implicit val pbClass: Class[_ <: PersistenceBroker] = pb.getClass
  def get(startDate: Option[String]) = Action.async {
    val request = new ApClassInstancesRequest(startDate)
    request.getFuture.map(s => {
      Ok(s).as("application/json")
    })
  }

  class ApClassInstancesRequest(startDateRaw: Option[String]) extends ApiRequest(cb) {
    def getCacheBrokerKey: CacheKey =
      "ap-class-instances-" + params.startDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))

    def getExpirationTime: LocalDateTime = {
      LocalDateTime.now.plusSeconds(10)
    }

    object params {
      val startDate: LocalDate = {
        val startDateDefault = LocalDate.now
        startDateRaw match {
          case None => startDateDefault
          case Some(d) => {
            "^[0-9]{2}\\/[0-9]{2}\\/[0-9]{4}$".r.findFirstIn(d)
            match {
              case Some(_) => LocalDate.parse(d, DateTimeFormatter.ofPattern("MM/dd/yyyy"))
              case None => startDateDefault
            }
          }
        }
      }
    }

    def getJSONResultFuture: Future[JsObject] = Future {
      val profiler = new Profiler

      val sessions: List[ApClassSession] = pb.getObjectsByFilters(
        ApClassSession,
        List(ApClassSession.fields.sessionDateTime.isDateConstant(params.startDate)),
        1500
      )

      val instances: List[ApClassInstance] = pb.getObjectsByIds(
        ApClassInstance,
        sessions.map(s => s.values.instanceId.get).distinct,
        500
      )

      val formats: List[ApClassFormat] = pb.getObjectsByIds(
        ApClassFormat,
        instances.map(i => i.values.formatId.get).distinct,
        500
      )

      val types: List[ApClassType] = pb.getObjectsByIds(
        ApClassType,
        formats.map(f => f.values.typeId.get).distinct,
        500
      )

      val signups: List[ApClassSignup] = pb.getObjectsByFilters(
        ApClassSignup,
        List(
          ApClassSignup.fields.instanceId.inList(instances.map(i => i.values.instanceId.get).distinct),
          ApClassSignup.fields.signupType.equalsConstant("E")
        ),
        500
      )


      profiler.lap("did all the databasing")

      val instancesHash: mutable.HashMap[Int, ApClassInstance] = {
        val h = new mutable.HashMap[Int, ApClassInstance]
        instances.foreach(i => h.put(i.values.instanceId.get, i))
        h
      }

      formats.foreach(f => {
        f.setApClassType(types.filter(_.values.typeId.get == f.values.typeId.get).head)
      })

      instances.foreach(i => {
        i.setApClassFormat(formats.filter(_.values.formatId.get == i.values.formatId.get).head)
      })

      sessions.foreach(s => {
        instancesHash.get(s.values.instanceId.get) match {
          case Some(i) => s.setApClassInstance(i);
          case None =>
        }
      })

      val sessionsSorted = sessions.sortWith((s1: ApClassSession, s2: ApClassSession) => {
        val displayOrder1 = s1.getApClassInstance.getApClassFormat.getApClassType.values.displayOrder.get
        val displayOrder2 = s2.getApClassInstance.getApClassFormat.getApClassType.values.displayOrder.get
        val instanceId1 = s1.getApClassInstance.values.instanceId.get
        val instanceId2 = s2.getApClassInstance.values.instanceId.get
        CascadeSort.sort(s1.values.sessionDateTime.get, s2.values.sessionDateTime.get)
          .sort(displayOrder1, displayOrder2)
          .sort(instanceId1, instanceId2)
          .end()
      })

      val sessionsJsArray: JsArray = JsArray(sessionsSorted.map(s => {
        val i: ApClassInstance = s.references.apClassInstance match {
          case Some(i1) => i1
          case None => throw new Exception()
        }
        val f: ApClassFormat = i.references.apClassFormat match {
          case Some(f1) => f1
          case None => throw new Exception()
        }
        val t: ApClassType = f.references.apClassType match {
          case Some(t1) => t1
          case None => throw new Exception()
        }

        JsArray(IndexedSeq(
          JsNumber(i.values.instanceId.get),
          JsString(t.values.typeName.get),
          JsString(s.values.sessionDateTime.get.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))),
          JsString(s.values.sessionDateTime.get.format(DateTimeFormatter.ofPattern("hh:mma"))),
          i.values.locationString.value match {
            case Some(x) => JsString(x)
            case None => JsNull
          },
          JsNumber(signups.count(signup => signup.values.instanceId.get == s.values.instanceId.get))
        ))
      }))

      val metaData = JsonUtil.getMetaData(Seq(
        "INSTANCE_ID",
        "TYPE_NAME",
        "START_DATE",
        "START_TIME",
        "LOCATION_STRING",
        "ENROLLEES"
      ))

      val data = JsObject(Map(
        "rows" -> sessionsJsArray,
        "metaData" -> metaData
      ))
      data
    }
  }
}
