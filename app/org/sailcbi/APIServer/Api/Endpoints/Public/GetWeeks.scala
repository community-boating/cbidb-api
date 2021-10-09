package org.sailcbi.APIServer.Api.Endpoints.Public

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.IO.JP.GetWeeks
import org.sailcbi.APIServer.IO.JP.GetWeeks.GetWeeksResult
import org.sailcbi.APIServer.UserTypes.PublicRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetWeeks @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async (req => {
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCache(PublicRequestCache)(None, parsedRequest, rc => {

			val weeks = GetWeeks.getWeeks(rc)

			implicit val format = GetWeeksResult.format
			Future(Ok(Json.toJson(weeks)))
		})
	})
}
