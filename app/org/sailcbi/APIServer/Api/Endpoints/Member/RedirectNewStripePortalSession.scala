package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.Api.ValidationResult
import org.sailcbi.APIServer.CbiUtil.{NetFailure, NetSuccess, ParsedRequest}
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.StripeError
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker, ResultSetWrapper}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.{ExecutionContext, Future}

class RedirectNewStripePortalSession @Inject()(implicit val exec: ExecutionContext, ws: WSClient) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(None, parsedRequest, rc => {
			val pb: PersistenceBroker = rc.pb

			val personId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)
			val customerIdOption = PortalLogic.getStripeCustomerId(pb, personId)

			customerIdOption match {
				case None => Future(Ok(ValidationResult.from("An internal error occurred.").toResultError.asJsObject()))
				case Some(id: String) => rc.getStripeIOController(ws).getCustomerPortalURL(id).map({
					case s: NetSuccess[String, _] => MovedPermanently(s.successObject)
					case f: NetFailure[_, StripeError] => Ok(ValidationResult.from("An internal error occurred.").toResultError.asJsObject())
				})
			}
		})
	}
}