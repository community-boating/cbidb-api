package org.sailcbi.APIServer.Api.Endpoints.Public

import java.time.LocalDateTime

import org.sailcbi.APIServer.Api.Endpoints.Public.JpTeams.JpTeamsParamsObject
import org.sailcbi.APIServer.Api.{AuthenticatedRequest, CacheableResultFromPreparedQuery, ParamsObject}
import org.sailcbi.APIServer.IO.PreparedQueries.Public.{GetJpTeams, GetJpTeamsResult}
import org.sailcbi.APIServer.Services.Authentication.PublicUserType
import javax.inject.Inject
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext

class JpTeams @Inject()(implicit val exec: ExecutionContext)
		extends AuthenticatedRequest with CacheableResultFromPreparedQuery[JpTeamsParamsObject, GetJpTeamsResult] {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = {
		evaluate(PublicUserType, new JpTeamsParamsObject, new GetJpTeams)
	}

	def getCacheBrokerKey(params: JpTeamsParamsObject): CacheKey = "jp-teams"

	def getExpirationTime: LocalDateTime = {
		LocalDateTime.now.plusSeconds(5)
	}
}

object JpTeams {

	class JpTeamsParamsObject extends ParamsObject

}
