package org.sailcbi.APIServer.Api.Endpoints.Public

import org.sailcbi.APIServer.Api.Endpoints.Public.JpClassInstances.JpClassInstancesParamsObject
import org.sailcbi.APIServer.Api.{CacheableResultFromPreparedQuery, ParamsObject}
import org.sailcbi.APIServer.CbiUtil.DateUtil
import org.sailcbi.APIServer.IO.PreparedQueries.Public.{GetJpClassInstances, GetJpClassInstancesResult}
import org.sailcbi.APIServer.Services.Authentication.PublicUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.mvc.{Action, AnyContent}

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class JpClassInstances @Inject()(implicit val exec: ExecutionContext)
		extends CacheableResultFromPreparedQuery[JpClassInstancesParamsObject, GetJpClassInstancesResult] {
	def get(startDate: Option[String])(implicit PA: PermissionsAuthority): Action[AnyContent] = {
		val params = JpClassInstancesParamsObject(DateUtil.parseWithDefault(startDate))
		val pq = new GetJpClassInstances(params.startDate)
		evaluate(PublicUserType, params, pq)
	}

	def getCacheBrokerKey(params: JpClassInstancesParamsObject): CacheKey =
		"jp-class-instances-" + params.startDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))

	def getExpirationTime: LocalDateTime = {
		LocalDateTime.now.plusSeconds(5)
	}
}

object JpClassInstances {

	case class JpClassInstancesParamsObject(startDate: LocalDate) extends ParamsObject

}
