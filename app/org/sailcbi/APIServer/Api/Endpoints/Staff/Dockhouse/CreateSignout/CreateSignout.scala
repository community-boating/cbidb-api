package org.sailcbi.APIServer.Api.Endpoints.Staff.Dockhouse.CreateSignout

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Logic.DockhouseLogic.CreateSignoutLogic.{CreateSignoutLogic, CreateSignoutRequest}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CreateSignout @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCache(StaffRequestCache)(None, parsedRequest, rc => {
			PA.withParsedPostBodyJSON(parsedRequest.postJSON, CreateSignoutRequest.apply)(parsed => {
				println(parsed)
				CreateSignoutLogic.attemptSignout(rc, parsed).fold(
					e => Future(BadRequest(e.asJsObject)),
					s => Future(Ok(Json.toJson(s)))
				)
			})
		})
	})
}

