package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.framework.API.ValidationResult
import com.coleji.framework.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.CbiUtil.{NetFailure, NetSuccess}
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.StripeError
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RedirectNewStripePortalSession @Inject()(implicit val exec: ExecutionContext, ws: WSClient) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(parsedRequest, rc => {
			val personId = rc.getAuthedPersonId()
			val customerIdOption = PortalLogic.getStripeCustomerId(rc, personId)

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