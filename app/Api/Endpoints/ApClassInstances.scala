package Api.Endpoints

import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter
import javax.inject.Inject

import Api.{ApiRequest, ApiRequestSync}
import CbiUtil.{CascadeSort, Profiler}
import Entities._
import Services.{CacheBroker, PersistenceBroker}
import play.api.inject.ApplicationLifecycle
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

import scala.collection.mutable

class ApClassInstances @Inject() (lifecycle: ApplicationLifecycle, cb: CacheBroker, pb: PersistenceBroker) extends Controller {
  implicit val pbClass: Class[_ <: PersistenceBroker] = pb.getClass
  def get(startDate: Option[String]) = Action {
    val request = ApClassInstancesRequest(startDate)
    Ok(request.get)
  }

  case class ApClassInstancesRequest(startDateRaw: Option[String]) extends ApiRequestSync(cb) {
    def getCacheBrokerKey: String =
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

    def getJSONResult: JsObject = {
      val profiler = new Profiler

      val sessions: List[ApClassSession] = pb.getObjectsByFilters(
        ApClassSession,
        List(ApClassSession.fields.sessionDateTime.isDateConstant(params.startDate)),
        1500
      )

      val instances: List[ApClassInstance] = pb.getObjectsByIds(
        ApClassInstance,
        sessions.map(s => s.instanceId).distinct,
        500
      )

      val formats: List[ApClassFormat] = pb.getObjectsByIds(
        ApClassFormat,
        instances.map(i => i.formatId).distinct,
        500
      )

      val types: List[ApClassType] = pb.getObjectsByIds(
        ApClassType,
        formats.map(f => f.typeId).distinct,
        500
      )

      val signups: List[ApClassSignup] = pb.getObjectsByFilters(
        ApClassSignup,
        List(
          ApClassSignup.fields.instanceId.inList(instances.map(i => i.instanceId).distinct),
          ApClassSignup.fields.signupType.equalsConstant("E")
        ),
        500
      )


      profiler.lap("did all the databasing")

      val instancesHash: mutable.HashMap[Int, ApClassInstance] = {
        val h = new mutable.HashMap[Int, ApClassInstance]
        instances.foreach(i => h.put(i.instanceId, i))
        h
      }

      formats.foreach(f => {
        f.setApClassType(types.filter(_.typeId == f.typeId).head)
      })

      instances.foreach(i => {
        i.setApClassFormat(formats.filter(_.formatId == i.formatId).head)
      })

      sessions.foreach(s => {
        instancesHash.get(s.instanceId) match {
          case Some(i) => s.setApClassInstance(i);
          case None =>
        }
      })

      val sessionsSorted = sessions.sortWith((s1: ApClassSession, s2: ApClassSession) => {
        val displayOrder1 = s1.getApClassInstance.getApClassFormat.getApClassType.displayOrder
        val displayOrder2 = s2.getApClassInstance.getApClassFormat.getApClassType.displayOrder
        val instanceId1 = s1.getApClassInstance.instanceId
        val instanceId2 = s2.getApClassInstance.instanceId
        CascadeSort.sort(s1.sessionDateTime, s2.sessionDateTime)
          .sort(displayOrder1, displayOrder2)
          .sort(instanceId1, instanceId2)
          .end()
      })

      val sessionsJsArray: JsArray = JsArray(sessionsSorted.map(s => {
        val i: ApClassInstance = s.references.apClassInstance match {
          case Some(i1) => i1
        }
        val f: ApClassFormat = i.references.apClassFormat match {
          case Some(f1) => f1
        }
        val t: ApClassType = f.references.apClassType match {
          case Some(t1) => t1
        }

        JsArray(IndexedSeq(
          JsNumber(i.instanceId),
          JsString(t.typeName),
          JsString(s.sessionDateTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))),
          JsString(s.sessionDateTime.format(DateTimeFormatter.ofPattern("hh:mma"))),
          JsString(i.locationString),
          JsNumber(signups.count(signup => signup.instanceId == s.instanceId))
        ))
      }))

      val metaData = JsArray(Seq(
        JsObject(Map("name" -> JsString("INSTANCE_ID"))),
        JsObject(Map("name" -> JsString("TYPE_NAME"))),
        JsObject(Map("name" -> JsString("START_DATE"))),
        JsObject(Map("name" -> JsString("START_TIME"))),
        JsObject(Map("name" -> JsString("LOCATION_STRING"))),
        JsObject(Map("name" -> JsString("ENROLLEES")))
      ))

      val data = JsObject(Map(
        "rows" -> sessionsJsArray,
        "metaData" -> metaData
      ))
      data
    }
  }
}
