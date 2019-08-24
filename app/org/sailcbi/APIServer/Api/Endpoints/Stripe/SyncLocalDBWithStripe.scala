package org.sailcbi.APIServer.Api.Endpoints.Stripe

import org.sailcbi.APIServer.Api.AuthenticatedRequest
import org.sailcbi.APIServer.CbiUtil.{CriticalError, ParsedRequest}
import org.sailcbi.APIServer.IO.Stripe.StripeIOController
import org.sailcbi.APIServer.Services.Authentication.ApexUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority
import javax.inject.Inject
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

class SyncLocalDBWithStripe @Inject()(ws: WSClient)(implicit exec: ExecutionContext) extends AuthenticatedRequest {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req => {
		val logger = PA.logger
		val rc = getRC(ApexUserType, ParsedRequest(req))
		val pb = rc.pb
		val stripeIOController = new StripeIOController(
			PA.stripeAPIIOMechanism.get(rc)(ws),
			PA.stripeDatabaseIOMechanism.get(rc)(pb),
			logger
		)
		stripeIOController.syncBalanceTransactions.map({
			case CriticalError(e) => {
				logger.error("Error syncing balance transactions", e)
				Ok("Error.")
			}
			case _ => Ok("balance transactions sync'd")
		})
	}
	}
}