package org.sailcbi.APIServer.Api.Endpoints.Stripe

import org.sailcbi.APIServer.CbiUtil.{NetFailure, NetSuccess, ParsedRequest}
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.PaymentMethod
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ClearCard @Inject()(ws: WSClient)(implicit val exec: ExecutionContext) extends InjectedController {
	def postAP()(implicit PA: PermissionsAuthority): Action[AnyContent] = post("AP")
	def postJP()(implicit PA: PermissionsAuthority): Action[AnyContent] = post("JP")

	private def post(program: String)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(None, parsedRequest, rc => {

			val stripeIOController = rc.getStripeIOController(ws)

			val personId = rc.auth.getAuthedPersonId(rc)
			val orderId = PortalLogic.getOrderId(rc, personId, program)
			val stripeCustomerId = PortalLogic.getStripeCustomerId(rc, personId)

			val orderHasStaggeredPayments = PortalLogic.getPaymentAdditionalMonths(rc, orderId) > 0

			if (orderHasStaggeredPayments) {
				stripeIOController.getCustomerDefaultPaymentMethod(stripeCustomerId.get).flatMap({
					case s: NetSuccess[Option[PaymentMethod], _] => s.successObject match {
						case Some(pm: PaymentMethod) => stripeIOController.detachPaymentMethod(pm.id).map(_ => Ok("done"))
						case None => Future(Ok("done"))
					}
					case f: NetFailure[_, _] => Future(Ok("error"))
				})
			} else {
				PortalLogic.clearStripeTokensFromOrder(rc, orderId)
				Future(Ok("done"))
			}
		})
	}
}