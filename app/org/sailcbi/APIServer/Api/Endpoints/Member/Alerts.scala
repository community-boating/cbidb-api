package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Entities.dto.PersonNotification.PersonAllNotificationsDto
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.MemberRequestCache
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class Alerts @Inject()(ws: WSClient)(implicit exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
			Future(Ok(Json.toJson(PortalLogic.getNotifications(rc, rc.getAuthedPersonId))))
		})
	})

	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
			PA.withParsedPostBodyJSON(req.body.asJson, PersonAllNotificationsDto.apply)(parsed => {
				Future(Ok(Json.toJson(PortalLogic.putNotifications(rc, rc.getAuthedPersonId, parsed))))
			})
		})
	})
}
