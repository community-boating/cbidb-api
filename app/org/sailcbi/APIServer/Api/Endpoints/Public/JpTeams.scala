package org.sailcbi.APIServer.Api.Endpoints.Public

import com.coleji.neptune.API.{CacheableResultFromPreparedQuery, ParamsObject}
import com.coleji.neptune.Core.PermissionsAuthority
import org.sailcbi.APIServer.Api.Endpoints.Public.JpTeams.JpTeamsParamsObject
import org.sailcbi.APIServer.IO.PreparedQueries.Public.{GetJpTeams, GetJpTeamsResult}
import org.sailcbi.APIServer.UserTypes.PublicRequestCache
import play.api.mvc.{Action, AnyContent}

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class JpTeams @Inject()(implicit val exec: ExecutionContext)
		extends CacheableResultFromPreparedQuery[JpTeamsParamsObject, GetJpTeamsResult] {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = {
		evaluate(PublicRequestCache, new JpTeamsParamsObject, new GetJpTeams)
	}

	def getCacheBrokerKey(params: JpTeamsParamsObject): CacheKey = "jp-teams"

	def getExpirationTime: LocalDateTime = {
		LocalDateTime.now.plusSeconds(5)
	}
}

object JpTeams {

	class JpTeamsParamsObject extends ParamsObject

}
