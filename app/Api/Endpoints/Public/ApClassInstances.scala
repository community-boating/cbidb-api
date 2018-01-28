package Api.Endpoints.Public

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}
import javax.inject.Inject

import Api.Endpoints.Public.ApClassInstances.ApClassInstancesParamsObject
import Api.{ParamsObject, PublicRequestFromPreparedQuery}
import CbiUtil.DateUtil
import Logic.PreparedQueries.Public.{GetApClassInstances, GetApClassInstancesResult}
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext

class ApClassInstances @Inject() (implicit exec: ExecutionContext) extends PublicRequestFromPreparedQuery[ApClassInstancesParamsObject, GetApClassInstancesResult] {
  def get(startDate: Option[String]): Action[AnyContent] = {
    val params = ApClassInstancesParamsObject(DateUtil.parseWithDefault(startDate))
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