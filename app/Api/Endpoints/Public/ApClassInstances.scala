package Api.Endpoints.Public

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}

import Api.Endpoints.Public.ApClassInstances.ApClassInstancesParamsObject
import Api.{AuthenticatedRequest, CacheableResultFromPreparedQuery, ParamsObject}
import CbiUtil.DateUtil
import IO.PreparedQueries.Public.{GetApClassInstances, GetApClassInstancesResult}
import Services.Authentication.PublicUserType
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext

class ApClassInstances @Inject()(implicit val exec: ExecutionContext)
		extends AuthenticatedRequest with CacheableResultFromPreparedQuery[ApClassInstancesParamsObject, GetApClassInstancesResult] {
	def get(startDate: Option[String]): Action[AnyContent] = {
		val params = ApClassInstancesParamsObject(DateUtil.parseWithDefault(startDate))
		val pq = new GetApClassInstances(params.startDate)
		evaluate(PublicUserType, params, pq)
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