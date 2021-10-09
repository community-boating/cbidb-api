package org.sailcbi.APIServer.Api.Endpoints.Public

import com.coleji.neptune.API.{CacheableResultFromPreparedQuery, ParamsObject}
import com.coleji.neptune.Core.PermissionsAuthority
import org.sailcbi.APIServer.Api.Endpoints.Public.FlagColorHistory.FlagColorHistoryParamsObject
import org.sailcbi.APIServer.IO.PreparedQueries.Public.{GetFlagColorHistory, GetFlagColorHistoryResult}
import org.sailcbi.APIServer.UserTypes.PublicRequestCache
import play.api.mvc.{Action, AnyContent}

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class FlagColorHistory  @Inject()(implicit val exec: ExecutionContext)
	extends CacheableResultFromPreparedQuery[FlagColorHistoryParamsObject, GetFlagColorHistoryResult] {
	def get48Hours()(implicit PA: PermissionsAuthority): Action[AnyContent] = {
		val params = new FlagColorHistoryParamsObject()
		val pq = new GetFlagColorHistory()
		evaluate(PublicRequestCache, params, pq)
	}

	def getCacheBrokerKey(params: FlagColorHistoryParamsObject): CacheKey = "flag-color-history"

	def getExpirationTime: LocalDateTime = {
		LocalDateTime.now.plusSeconds(5)
	}
}


object FlagColorHistory {

	class FlagColorHistoryParamsObject extends ParamsObject

}