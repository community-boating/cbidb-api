package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.neptune.API.ValidationResult
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Api.Endpoints.Dto.Member.ApTeachInstance.DtoMemberApTeachInstancePostRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.MemberRequestCache
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UnenrollTeachApInstance @Inject()(ws: WSClient)(implicit val exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
			val personId = rc.getAuthedPersonId
			PA.withParsedPostBodyJSON(request.body.asJson, DtoMemberApTeachInstancePostRequest.apply)(parsed => {
				PortalLogic.attemptUnenrollTeachApClass(rc, personId, parsed.instanceId) match {
					case None => Future(Ok("OK"))
					case Some(s) => Future(BadRequest(ValidationResult.from(s).toResultError.asJsObject))
				}
			})
		})
	}
}
