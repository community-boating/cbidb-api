package org.sailcbi.APIServer.Api.Endpoints.Public

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}

import javax.inject.Inject
import org.sailcbi.APIServer.Api.Endpoints.Public.ApClassInstances.ApClassInstancesParamsObject
import org.sailcbi.APIServer.Api.{CacheableResultFromPreparedQuery, ParamsObject}
import org.sailcbi.APIServer.CbiUtil.DateUtil
import org.sailcbi.APIServer.IO.PreparedQueries.Public.{GetApClassInstances, GetApClassInstancesResult}
import org.sailcbi.APIServer.Services.Authentication.PublicUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext

class ApClassInstances @Inject()(implicit val exec: ExecutionContext)
		extends CacheableResultFromPreparedQuery[ApClassInstancesParamsObject, GetApClassInstancesResult] {
	def get(startDate: Option[String])(implicit PA: PermissionsAuthority): Action[AnyContent] = {
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