package org.sailcbi.APIServer.Api.Endpoints.Public

import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

import javax.inject.Inject
import org.sailcbi.APIServer.Api.Endpoints.Public.StaticYearlyData.StaticYearlyDataParamsObject
import org.sailcbi.APIServer.Api.{AuthenticatedRequest, CacheableResultFromPreparedQuery, ParamsObject}
import org.sailcbi.APIServer.CbiUtil.DateUtil
import org.sailcbi.APIServer.IO.PreparedQueries.{HardcodedQueryForSelectCastableToJSObject, PreparedQueryForSelect}
import org.sailcbi.APIServer.IO.PreparedQueries.Public.{GetJpClassInstances, GetJpClassInstancesResult, GetStaticYearlyData, GetStaticYearlyDataResult}
import org.sailcbi.APIServer.Services.Authentication.{MemberUserType, ProtoPersonUserType, PublicUserType}
import org.sailcbi.APIServer.Services.{PermissionsAuthority, ResultSetWrapper}
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext

class StaticYearlyData @Inject()(implicit val exec: ExecutionContext)
		extends AuthenticatedRequest with CacheableResultFromPreparedQuery[StaticYearlyDataParamsObject, GetStaticYearlyDataResult] {
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