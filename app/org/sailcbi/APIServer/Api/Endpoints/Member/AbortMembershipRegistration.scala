package org.sailcbi.APIServer.Api.Endpoints.Member

import org.sailcbi.APIServer.Api.{ValidationError, ValidationOk}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AbortMembershipRegistration @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request => {
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, AbortMembershipRegistrationShape.apply)(parsed => {
			PA.withRequestCacheMemberWithJuniorId(None, parsedRequest, parsed.juniorId, rc => {
				val pb = rc.pb

				val parentPersonId = rc.auth.getAuthedPersonId(pb)

				PortalLogic.deleteRegistration(pb, parentPersonId, parsed.juniorId) match {
					case ValidationOk => Future(Ok(new JsObject(Map("success" -> JsBoolean(true)))))
					case e: ValidationError => Future(Ok(e.toResultError.asJsObject()))
				}
			})
		})
	}}

	case class AbortMembershipRegistrationShape (juniorId: Int)

	object AbortMembershipRegistrationShape {
		implicit val format = Json.format[AbortMembershipRegistrationShape]

		def apply(v: JsValue): AbortMembershipRegistrationShape = v.as[AbortMembershipRegistrationShape]
	}
}
