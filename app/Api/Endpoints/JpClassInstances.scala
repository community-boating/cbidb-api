package Api.Endpoints

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}
import javax.inject.Inject

import Api.ApiRequest
import CbiUtil.{CascadeSort, JsonUtil, Profiler}
import Entities.Entities._
import Services.PermissionsAuthority.UnauthorizedAccessException
import Services.{CacheBroker, PermissionsAuthority, PersistenceBroker, RequestCache}
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, Controller}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class JpClassInstances @Inject() (implicit exec: ExecutionContext) extends Controller {
  def get(startDate: Option[String]): Action[AnyContent] = Action.async {request =>
    try {
      val rc: RequestCache = PermissionsAuthority.getRequestCache(request)
      val pb: PersistenceBroker = rc.pb
      val cb: CacheBroker = rc.cb
      val apiRequest = new JpClassInstancesRequest(pb, cb, startDate)
      apiRequest.getFuture.map(s => {
        Ok(s).as("application/json")
      })
    } catch {
      case _: UnauthorizedAccessException => Future{ Ok("Access Denied") }
      case _: Throwable => Future{ Ok("Internal Error") }
    }
  }

  class JpClassInstancesRequest(pb: PersistenceBroker, cb: CacheBroker, startDateRaw: Option[String]) extends ApiRequest(cb) {
    def getCacheBrokerKey: CacheKey =
      "jp-class-instances-" + params.startDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))

    def getExpirationTime: LocalDateTime = {
      LocalDateTime.now.plusSeconds(5)
    }

    object params {
      val startDate: LocalDate = {
        val startDateDefault = LocalDate.now
        startDateRaw match {
          case None => startDateDefault
          case Some(d) => {
            "^[0-9]{2}\\/[0-9]{2}\\/[0-9]{4}$".r.findFirstIn(d) match {
              case Some(_) => LocalDate.parse(d, DateTimeFormatter.ofPattern("MM/dd/yyyy"))
              case None => startDateDefault
            }
          }
        }
      }
    }

    def getJSONResultFuture: Future[JsObject] = Future {
      val profiler = new Profiler

      val sessions: List[JpClassSession] = pb.getObjectsByFilters(
        JpClassSession,
        List(JpClassSession.fields.sessionDateTime.isDateConstant(params.startDate)),
        1500
      )

      val instances: List[JpClassInstance] = pb.getObjectsByIds(
        JpClassInstance,
        sessions.map(s => s.values.instanceId.get).distinct,
        500
      )

      val types: List[JpClassType] = pb.getObjectsByIds(
        JpClassType,
        instances.map(i => i.values.typeId.get).distinct,
        500
      )

      val locations: List[ClassLocation] = pb.getObjectsByIds(
        ClassLocation,
        instances.flatMap(i => i.values.locationId.get).distinct,
        500
      )

      val instructors: List[ClassInstructor] = pb.getObjectsByIds(
        ClassInstructor,
        instances.flatMap(i => i.values.instructorId.get).distinct,
        500
      )

      val signups: List[JpClassSignup] = pb.getObjectsByFilters(
        JpClassSignup,
        List(
          JpClassSignup.fields.instanceId.inList(instances.map(i => i.values.instanceId.get).distinct),
          JpClassSignup.fields.signupType.equalsConstant("E")
        ),
        500
      )


      profiler.lap("did all the databasing")

      val instancesHash: mutable.HashMap[Int, JpClassInstance] = {
        val h = new mutable.HashMap[Int, JpClassInstance]
        instances.foreach(i => h.put(i.values.instanceId.get, i))
        h
      }

      instances.foreach(i => {
        i.setJpClassType(types.filter(t => t.values.typeId.get == i.values.typeId.get).head)

        i.values.instructorId.get match {
          case Some(x) => i.setClassInstructor(Some(instructors.filter(ins => ins.values.instructorId.get == x).head))
          case None => i.setClassInstructor(None)
        }

        i.values.locationId.get match {
          case Some(x) => i.setClassLocation(Some(locations.filter(l => l.values.locationId.get == x).head))
          case None => i.setClassLocation(None)
        }
      })

      sessions.foreach(s => {
        instancesHash.get(s.values.instanceId.get) match {
          case Some(i) => s.setJpClassInstance(i);
          case None =>
        }
      })

      val sessionsSorted = sessions.sortWith((s1: JpClassSession, s2: JpClassSession) => {
        val displayOrder1 = s1.getJpClassInstance.getJpClassType.values.displayOrder.get
        val displayOrder2 = s2.getJpClassInstance.getJpClassType.values.displayOrder.get
        val instanceId1 = s1.getJpClassInstance.values.instanceId.get
        val instanceId2 = s2.getJpClassInstance.values.instanceId.get
        CascadeSort.sort(s1.values.sessionDateTime.get, s2.values.sessionDateTime.get)
          .sort(displayOrder1, displayOrder2)
          .sort(instanceId1, instanceId2)
          .end()
      })

      val sessionsJsArray: JsArray = JsArray(sessionsSorted.map(s => {
        val i: JpClassInstance = s.references.jpClassInstance match {
          case Some(i1) => i1
          case None => throw new Exception("wut")
        }
        val t: JpClassType = i.references.jpClassType match {
          case Some(t1) => t1
          case None => throw new Exception("wut")
        }
        val l: Option[ClassLocation] = i.references.classLocation match {
          case Some(l1) => l1
          case None => throw new Exception("wut")
        }
        val ins: Option[ClassInstructor] = i.references.classInstructor match {
          case Some(ins1) => ins1
          case None => throw new Exception("wut")
        }
        JsArray(IndexedSeq(
          JsNumber(i.values.instanceId.get),
          JsString(t.values.typeName.get),
          JsString(s.values.sessionDateTime.get.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))),
          JsString(s.values.sessionDateTime.get.format(DateTimeFormatter.ofPattern("hh:mma"))),
          l match { case Some(l1) => JsString(l1.values.locationName.get); case None => JsNull },
          ins match { case Some(ins1) => JsString(ins1.values.nameFirst.get); case None => JsNull },
          ins match { case Some(ins1) => JsString(ins1.values.nameLast.get); case None => JsNull },
          JsNumber(signups.count(signup => signup.values.instanceId.get == s.values.instanceId.get))
        ))
      }))

      val metaData = JsonUtil.getMetaData(Seq(
        "INSTANCE_ID",
        "TYPE_NAME",
        "START_DATE",
        "START_TIME",
        "LOCATION_NAME",
        "INSTRUCTOR_NAME_FIRST",
        "INSTRUCTOR_NAME_LAST",
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
