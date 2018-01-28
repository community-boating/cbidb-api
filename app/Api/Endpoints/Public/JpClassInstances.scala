package Api.Endpoints.Public

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}
import javax.inject.Inject

import Api.Endpoints.Public.JpClassInstances.JpClassInstancesParamsObject
import Api.{ParamsObject, PublicRequestFromPreparedQuery}
import CbiUtil.DateUtil
import Logic.PreparedQueries.Public.{GetJpClassInstances, GetJpClassInstancesResult}
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext

class JpClassInstances @Inject() (implicit exec: ExecutionContext) extends PublicRequestFromPreparedQuery[JpClassInstancesParamsObject, GetJpClassInstancesResult] {
  def get(startDate: Option[String]): Action[AnyContent] = {
    val params = JpClassInstancesParamsObject(DateUtil.parseWithDefault(startDate))
    val pq = new GetJpClassInstances(params.startDate)
    evaluate(params, pq)
  }

  def getCacheBrokerKey(params: JpClassInstancesParamsObject): CacheKey =
    "ap-class-instances-" + params.startDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))

  def getExpirationTime: LocalDateTime = {
    LocalDateTime.now.plusSeconds(5)
  }
}

object JpClassInstances {
  case class JpClassInstancesParamsObject(startDate: LocalDate) extends ParamsObject
}