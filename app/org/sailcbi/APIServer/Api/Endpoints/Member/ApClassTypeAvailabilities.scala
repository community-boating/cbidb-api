package org.sailcbi.APIServer.Api.Endpoints.Member

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Services.{CacheBroker, PermissionsAuthority}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApClassTypeAvailabilities @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(None, parsedRequest, rc => {
			val pb = rc.pb
			val cb: CacheBroker = rc.cb
			val personId = rc.auth.getAuthedPersonId(pb)

			val resultObj = PortalLogic.getApClassTypeAvailabilities(pb, personId)
			Future(Ok(Json.toJson(resultObj)))
		})
	}
}
