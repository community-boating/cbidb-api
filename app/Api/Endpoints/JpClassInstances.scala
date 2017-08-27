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

class JpClassInstances @Inject() (lifecycle: ApplicationLifecycle, cb: CacheBroker, pb: PersistenceBroker) extends Controller {
  implicit val pbClass: Class[_ <: PersistenceBroker] = pb.getClass
  def get(startDate: Option[String]) = Action {
    val request = JpClassInstancesRequest(startDate)
    Ok(request.get)
  }

  case class JpClassInstancesRequest(startDateRaw: Option[String]) extends ApiRequestSync(cb) {
    def getCacheBrokerKey: String =
      "jp-class-instances-" + params.startDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))

    def getExpirationTime: LocalDateTime = {
      LocalDateTime.now.plusSeconds(10)
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

    def getJSONResult: JsObject = {
      val profiler = new Profiler

      val sessions: List[JpClassSession] = pb.getObjectsByFilters(
        JpClassSession,
        List(JpClassSession.fields.sessionDateTime.isDateConstant(params.startDate)),
        1500
      )

      val instances: List[JpClassInstance] = pb.getObjectsByIds(
        JpClassInstance,
        sessions.map(s => s.instanceId).distinct,
        500
      )

      val types: List[JpClassType] = pb.getObjectsByIds(
        JpClassType,
        instances.map(i => i.typeId).distinct,
        500
      )

      val locations: List[ClassLocation] = pb.getObjectsByIds(
        ClassLocation,
        instances.flatMap(i => i.locationId).distinct,
        500
      )

      val instructors: List[ClassInstructor] = pb.getObjectsByIds(
        ClassInstructor,
        instances.flatMap(i => i.instructorId).distinct,
        500
      )

      val signups: List[JpClassSignup] = pb.getObjectsByFilters(
        JpClassSignup,
        List(
          JpClassSignup.fields.instanceId.inList(instances.map(i => i.instanceId).distinct),
          JpClassSignup.fields.signupType.equalsConstant("E")
        ),
        500
      )


      profiler.lap("did all the databasing")

      val instancesHash: mutable.HashMap[Int, JpClassInstance] = {
        val h = new mutable.HashMap[Int, JpClassInstance]
        instances.foreach(i => h.put(i.instanceId, i))
        h
      }

      instances.foreach(i => {
        i.setJpClassType(types.filter(t => t.typeId == i.typeId).head)

        i.instructorId match {
          case Some(x) => i.setClassInstructor(Some(instructors.filter(ins => ins.instructorId == x).head))
          case None => i.setClassInstructor(None)
        }

        i.locationId match {
          case Some(x) => i.setClassLocation(Some(locations.filter(l => l.locationId == x).head))
          case None => i.setClassLocation(None)
        }
      })

      sessions.foreach(s => {
        instancesHash.get(s.instanceId) match {
          case Some(i) => s.setJpClassInstance(i);
          case None =>
        }
      })

      val sessionsSorted = sessions.sortWith((s1: JpClassSession, s2: JpClassSession) => {
        val displayOrder1 = s1.getJpClassInstance.getJpClassType.displayOrder
        val displayOrder2 = s2.getJpClassInstance.getJpClassType.displayOrder
        val instanceId1 = s1.getJpClassInstance.instanceId
        val instanceId2 = s2.getJpClassInstance.instanceId
        CascadeSort.sort(s1.sessionDateTime, s2.sessionDateTime)
          .sort(displayOrder1, displayOrder2)
          .sort(instanceId1, instanceId2)
          .end()
      })

      val sessionsJsArray: JsArray = JsArray(sessionsSorted.map(s => {
        val i: JpClassInstance = s.references.jpClassInstance match {
          case Some(i1) => i1
        }
        val t: JpClassType = i.references.jpClassType match {
          case Some(t1) => t1
        }
        val l: Option[ClassLocation] = i.references.classLocation match {
          case Some(l1) => l1
        }
        val ins: Option[ClassInstructor] = i.references.classInstructor match {
          case Some(ins1) => ins1
        }
        JsArray(IndexedSeq(
          JsNumber(i.instanceId),
          JsString(t.typeName),
          JsString(s.sessionDateTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))),
          JsString(s.sessionDateTime.format(DateTimeFormatter.ofPattern("hh:mma"))),
          l match { case Some(l1) => JsString(l1.locationName); case None => JsNull },
          ins match { case Some(ins1) => JsString(ins1.nameFirst); case None => JsNull },
          ins match { case Some(ins1) => JsString(ins1.nameLast); case None => JsNull },
          JsNumber(signups.count(signup => signup.instanceId == s.instanceId))
        ))
      }))

      val metaData = JsArray(Seq(
        JsObject(Map("name" -> JsString("INSTANCE_ID"))),
        JsObject(Map("name" -> JsString("TYPE_NAME"))),
        JsObject(Map("name" -> JsString("START_DATE"))),
        JsObject(Map("name" -> JsString("START_TIME"))),
        JsObject(Map("name" -> JsString("LOCATION_NAME"))),
        JsObject(Map("name" -> JsString("INSTRUCTOR_NAME_FIRST"))),
        JsObject(Map("name" -> JsString("INSTRUCTOR_NAME_LAST"))),
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
