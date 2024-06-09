package org.sailcbi.APIServer.Api.Endpoints.Public

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Entities.EntityDefinitions.SunsetTime
import org.sailcbi.APIServer.Entities.cacheable.sunset.{SunsetCache, SunsetCacheKey}
import org.sailcbi.APIServer.UserTypes.PublicRequestCache
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class Sunset @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def get(year: Int, month: Int, day: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(PublicRequestCache)(None, ParsedRequest(req), rc => {
			val (rows, _) = SunsetCache.get(rc, SunsetCacheKey(year, month, Option(day)))
//			implicit val writes: Writes[SunsetTime] = SunsetTime.storableJsonWrites
			rows.find(_.values.forDate.get.getDayOfMonth == day) match {
				case Some(r) => Future(Ok(Json.toJson(r)))
				case None => Future(NotFound)
			}
		})
	})
}
