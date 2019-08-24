package org.sailcbi.APIServer.Api.Endpoints.Stripe

import org.sailcbi.APIServer.Api.AuthenticatedRequest
import org.sailcbi.APIServer.CbiUtil.{CriticalError, NetSuccess, ParsedRequest, ValidationError}
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.{StripeError, Token}
import org.sailcbi.APIServer.IO.Stripe.StripeIOController
import org.sailcbi.APIServer.Services.Authentication.ApexUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority
import javax.inject.Inject
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext

class GetTokenDetails @Inject()(ws: WSClient)(implicit exec: ExecutionContext) extends AuthenticatedRequest {
	def get(token: String): Action[AnyContent] = Action.async { req => {
		val logger = PermissionsAuthority.logger
		val rc = getRC(ApexUserType, ParsedRequest(req))
		val pb = rc.pb
		val stripeIOController = new StripeIOController(
			PermissionsAuthority.stripeAPIIOMechanism.get(rc)(ws),
			PermissionsAuthority.stripeDatabaseIOMechanism.get(rc)(pb),
			logger
		)

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
	}
	}
}
