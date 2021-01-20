package org.sailcbi.APIServer.Api.Endpoints.Stripe

import org.sailcbi.APIServer.CbiUtil.{CriticalError, ParsedRequest, Succeeded}
import org.sailcbi.APIServer.Services.Authentication.ApexUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class SyncLocalDBWithStripe @Inject()(ws: WSClient)(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req => {
		val logger = PA.logger
		PA.withRequestCache(ApexUserType)(None, ParsedRequest(req), rc => {
			val pb = rc.pb
			val stripeIOController = rc.getStripeIOController(ws)
			stripeIOController.syncBalanceTransactions.map({
				case CriticalError(e) => {
					logger.error("Error syncing balance transactions", e)
					Ok("Error.")
				}
				case Succeeded(r) => Ok(s"balance transactions sync'd; completed ${r._1} inserts, ${r._2} updates, ${r._3} deletes.")
			})
		})
	}}
}