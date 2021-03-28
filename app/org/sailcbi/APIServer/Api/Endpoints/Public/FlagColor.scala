package org.sailcbi.APIServer.Api.Endpoints.Public

import com.coleji.framework.API.{CacheableResultFromPreparedQuery, ParamsObject}
import com.coleji.framework.Core.{CacheBroker, PermissionsAuthority}
import org.sailcbi.APIServer.Api.Endpoints.Public.FlagColor.FlagColorParamsObject
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.PreparedQueries.Public.{GetFlagColor, GetFlagColorResult}
import org.sailcbi.APIServer.UserTypes.PublicRequestCache
import play.api.mvc.{Action, AnyContent}

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class FlagColor @Inject()(implicit val exec: ExecutionContext)
		extends CacheableResultFromPreparedQuery[FlagColorParamsObject, GetFlagColorResult] {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		PA.withRequestCache(PublicRequestCache)(None, ParsedRequest(request), rc => {
			val cb: CacheBroker = rc.cb

			getFuture(cb, rc, new FlagColorParamsObject, new GetFlagColor).map(s => {
				val r = ".*\\[\\[\"([A-Z])\"\\]\\].*".r
				val f = s.toString match {
					case r(flag: String) => flag
					case _ => "C"
				}

				Ok("var FLAG_COLOR = \"" + f + "\"")
			})
		})
	}

	def getCacheBrokerKey(params: FlagColorParamsObject): CacheKey = "flag-color"

	def getExpirationTime: LocalDateTime = {
		LocalDateTime.now.plusSeconds(5)
	}
}

object FlagColor {

	class FlagColorParamsObject extends ParamsObject

}
