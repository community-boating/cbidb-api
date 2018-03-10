package Api.Endpoints.Public

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}
import javax.inject.Inject

import Api.Endpoints.Public.JpClassInstances.JpClassInstancesParamsObject
import Api.{AuthenticatedRequest, CacheableResultFromPreparedQuery, ParamsObject}
import CbiUtil.DateUtil
import IO.PreparedQueries.Public.{GetJpClassInstances, GetJpClassInstancesResult}
import Services.Authentication.PublicUserType
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext

class JpClassInstances @Inject() (implicit val exec: ExecutionContext)
extends AuthenticatedRequest with CacheableResultFromPreparedQuery[JpClassInstancesParamsObject, GetJpClassInstancesResult] {
  def get(startDate: Option[String]): Action[AnyContent] = {
    val params = JpClassInstancesParamsObject(DateUtil.parseWithDefault(startDate))
    val pq = new GetJpClassInstances(params.startDate)
    evaluate(PublicUserType, params, pq)
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