package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.Api.ValidationResult
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker}
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.{ExecutionContext, Future}

class ApClassSignup @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(None, parsedRequest, rc => {
			val pb: PersistenceBroker = rc.pb
			val personId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)
			PA.withParsedPostBodyJSON(request.body.asJson, ApClassSignupShape.apply)(parsed => {
				PortalLogic.apClassSignup(pb, personId, parsed.instanceId, parsed.doWaitlist) match {
					case None => Future(Ok(new JsObject(Map(
						"success" -> JsBoolean(true)
					))))
					case Some(err: String) => Future(Ok(ValidationResult.from(err).toResultError.asJsObject()))
				}


			})
		})
	}

	case class ApClassSignupShape (
		instanceId: Int,
		doWaitlist: Boolean
	)

	object ApClassSignupShape {
		implicit val format = Json.format[ApClassSignupShape]

		def apply(v: JsValue): ApClassSignupShape = v.as[ApClassSignupShape]
	}
}