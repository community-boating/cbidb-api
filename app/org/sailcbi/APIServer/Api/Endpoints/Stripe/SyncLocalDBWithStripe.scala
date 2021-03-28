package org.sailcbi.APIServer.Api.Endpoints.Stripe

import com.coleji.framework.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.framework.Util.{CriticalError, Succeeded}
import org.sailcbi.APIServer.UserTypes.ApexRequestCache
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class SyncLocalDBWithStripe @Inject()(ws: WSClient)(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req => {
		val logger = PA.logger
		PA.withRequestCache(ApexRequestCache)(None, ParsedRequest(req), rc => {

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