package org.sailcbi.APIServer.Api.Endpoints.Public

import org.sailcbi.APIServer.Api.Endpoints.Public.StaticYearlyData.StaticYearlyDataParamsObject
import org.sailcbi.APIServer.Api.{CacheableResultFromPreparedQuery, ParamsObject}
import org.sailcbi.APIServer.IO.PreparedQueries.Public.{GetStaticYearlyData, GetStaticYearlyDataResult}
import org.sailcbi.APIServer.Services.Authentication.PublicUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.mvc.{Action, AnyContent}

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class StaticYearlyData @Inject()(implicit val exec: ExecutionContext)
		extends CacheableResultFromPreparedQuery[StaticYearlyDataParamsObject, GetStaticYearlyDataResult] {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = {
		val params = StaticYearlyDataParamsObject()
		val pq = new GetStaticYearlyData
		evaluate(PublicUserType, params, pq)
	}

	def getCacheBrokerKey(params: StaticYearlyDataParamsObject): CacheKey = "static-yearly-data"

	def getExpirationTime: LocalDateTime = {
		LocalDateTime.now.plusSeconds(5)
	}
}

object StaticYearlyData {

	case class StaticYearlyDataParamsObject() extends ParamsObject

}
