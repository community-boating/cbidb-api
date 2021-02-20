package org.sailcbi.APIServer.Api.Endpoints.Member

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Services.Authentication.ProtoPersonRequestCache
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DeleteJuniorClassReservation @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val logger = PA.logger
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(ProtoPersonRequestCache)(None, parsedRequest, rc => {
			parsedRequest.postParams.get("name") match {
				case None => {
					println("no body")
					Future(new Status(400)("no body"))
				}
				case Some(name: String) => {
					println(name)

					PortalLogic.deleteProtoJunior(rc, rc.getAuthedPersonId().get, name)
					Future(Ok("deleted"))
				}
			}
		})
	}
}