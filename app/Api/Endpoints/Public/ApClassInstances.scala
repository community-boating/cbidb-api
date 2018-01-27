package Api.Endpoints.Public

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}
import javax.inject.Inject

import Api.Endpoints.Public.ApClassInstances.ApClassInstancesParamsObject
import Api.Endpoints.Public.JpTeams.JpTeamsParamsObject
import Api.{ParamsObject, PublicRequest}
import CbiUtil.Profiler
import Entities.EntityDefinitions.ApClassSession
import Logic.PreparedQueries.Public.{GetApClassInstances, GetApClassInstancesResult, GetJpTeams}
import Services.PersistenceBroker
import play.api.libs.json.{JsArray, JsObject, JsString}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}

class ApClassInstances @Inject() (implicit exec: ExecutionContext) extends PublicRequest[ApClassInstancesParamsObject, GetApClassInstancesResult] {
  def get(startDate: Option[String]): Action[AnyContent] = {
    val startDateCast: LocalDate = {
      val startDateDefault = LocalDate.now
      startDate match {
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
    val params = ApClassInstancesParamsObject(startDateCast)
    val pq = new GetApClassInstances(params.startDate)
    evaluate(params, pq)
  }

  def getCacheBrokerKey(params: ApClassInstancesParamsObject): CacheKey =
    "ap-class-instances-" + params.startDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))

  def getExpirationTime: LocalDateTime = {
    LocalDateTime.now.plusSeconds(5)
  }
}

object ApClassInstances {
  case class ApClassInstancesParamsObject(startDate: LocalDate) extends ParamsObject
}