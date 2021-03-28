package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.framework.Core.{CacheBroker, ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApClassTypeAvailabilities @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(parsedRequest, rc => {
			val cb: CacheBroker = rc.cb
			val personId = rc.getAuthedPersonId()

			val resultObj = PortalLogic.getApClassTypeAvailabilities(rc, personId)
			Future(Ok(Json.toJson(resultObj)))
		})
	}
}
