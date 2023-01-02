package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.neptune.API.{ValidationError, ValidationOk}
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.MemberRequestCache
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AbortMembershipRegistration @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request => {
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, AbortMembershipRegistrationShape.apply)(parsed => {
			MemberRequestCache.withRequestCacheMemberWithJuniorId(parsedRequest, parsed.juniorId, rc => {

				val parentPersonId = rc.getAuthedPersonId

				PortalLogic.deleteRegistration(rc, parentPersonId, parsed.juniorId) match {
					case ValidationOk => Future(Ok(new JsObject(Map("success" -> JsBoolean(true)))))
					case e: ValidationError => Future(Ok(e.toResultError.asJsObject))
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
