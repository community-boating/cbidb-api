package org.sailcbi.APIServer.Api.Endpoints.Stripe

import org.sailcbi.APIServer.CbiUtil.{CriticalError, NetSuccess, ParsedRequest, ValidationError}
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.{StripeError, Token}
import org.sailcbi.APIServer.Services.Authentication.ApexUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

// Called by apex to get details so apex can save to prod db itself.  Legacy
class GetTokenDetails @Inject()(ws: WSClient)(implicit val exec: ExecutionContext) extends InjectedController {
	def get(token: String)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req => {
		val logger = PA.logger
		PA.withRequestCache(ApexUserType)(None, ParsedRequest(req), rc => {
			val pb = rc.pb
			val stripeIOController = rc.getStripeIOController(ws)

			stripeIOController.getTokenDetails(token).map({
				case s: NetSuccess[Token, StripeError] => {
					println("Get token details success " + s.successObject)
					Ok(List(
						"success",
						s.successObject.id,
						s.successObject.used,
						s.successObject.card.last4,
						s.successObject.card.exp_month,
						s.successObject.card.exp_year,
						s.successObject.card.address_zip.getOrElse("")
					).mkString("$$"))
				}
				case v: ValidationError[Token, StripeError] => {
					logger.warning("Get token details validation error " + v.errorObject)
					Ok(List("failure", v.errorObject.`type`, v.errorObject.message).mkString("$$"))
				}
				case e: CriticalError[Token, StripeError] => {
					logger.error("Get token details critical error: ", e.e)
					Ok(List("failure", "cbi-api-error", e.e.getMessage).mkString("$$"))
				}
			})
		})
	}}
}
