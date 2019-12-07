package org.sailcbi.APIServer.Api.Endpoints.Public

import java.time.LocalDateTime

import javax.inject.Inject
import org.sailcbi.APIServer.Api.Endpoints.Public.JpClassInstancesWithAvail.JpClassInstancesWithAvailParamsObject
import org.sailcbi.APIServer.Api.{CacheableResultFromPreparedQuery, ParamsObject}
import org.sailcbi.APIServer.IO.PreparedQueries.Member.{GetClassInstancesQuery, GetClassInstancesQueryResult}
import org.sailcbi.APIServer.Services.Authentication.PublicUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext

class JpClassInstancesWithAvail @Inject()(implicit val exec: ExecutionContext)
		extends CacheableResultFromPreparedQuery[JpClassInstancesWithAvailParamsObject, GetClassInstancesQueryResult] {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = {
		val params = new JpClassInstancesWithAvailParamsObject()
		val pq = GetClassInstancesQuery.public()
		evaluate(PublicUserType, params, pq)
	}

	def getCacheBrokerKey(params: JpClassInstancesWithAvailParamsObject): CacheKey = "jp-class-instances-avail"

	def getExpirationTime: LocalDateTime = {
		LocalDateTime.now.plusSeconds(1)
	}
}

object JpClassInstancesWithAvail {

	class JpClassInstancesWithAvailParamsObject extends ParamsObject

}
