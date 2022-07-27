package org.sailcbi.APIServer.Api.Endpoints.Discord

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.{MemberRequestCache, PublicRequestCache}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VerifyMember @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCache(PublicRequestCache)(None, parsedRequest, rc => {
			PA.withParsedPostBodyJSON(parsedRequest.postJSON, VerifyMemberShape.apply)(parsed => {
				PortalLogic.discordVerifyToken(rc, parsed.verificationToken, parsed.discordUserId, parsed.discordUserName, parsed.discordUserDiscriminator) match {
					case None => Future(BadRequest)
					case Some(personId) => Future(Ok(Json.toJson(personId)))
				}
			})
		})
	})

	case class VerifyMemberShape(
		discordUserId: String,
		verificationToken: String,
		discordUserName: String,
		discordUserDiscriminator: String
	)

	object VerifyMemberShape {
		implicit val format = Json.format[VerifyMemberShape]

		def apply(v: JsValue): VerifyMemberShape = v.as[VerifyMemberShape]
	}
}
