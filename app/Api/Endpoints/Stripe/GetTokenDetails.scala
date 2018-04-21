package Api.Endpoints.Stripe

import java.time.ZonedDateTime

import javax.inject.Inject
import Api.AuthenticatedRequest
import CbiUtil.{CriticalError, NetSuccess, ParsedRequest, ValidationError}
import Entities.JsFacades.Stripe.{Charge, StripeError, Token}
import IO.Stripe.StripeIOController
import Services.Authentication.ApexUserType
import Services.PermissionsAuthority
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.{ExecutionContext, Future}

class GetTokenDetails @Inject() (ws: WSClient) (implicit exec: ExecutionContext) extends AuthenticatedRequest {
  def get(token: String): Action[AnyContent] = Action.async {req => {
    val logger = PermissionsAuthority.logger
    val rc = getRC(ApexUserType, ParsedRequest(req))
    val pb = rc.pb
    val stripeIOController = new StripeIOController(
      PermissionsAuthority.stripeAPIIOMechanism.get(rc)(ws),
      PermissionsAuthority.stripeDatabaseIOMechanism.get(rc)(pb)
    )

    stripeIOController.updateLocalChargesFromAPIForClose(2184, ZonedDateTime.now(), None)

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
          s.successObject.card.address_zip
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
  }}
}
