package org.sailcbi.APIServer.Api.Endpoints.Stripe

import com.coleji.framework.Core.{PermissionsAuthority, RequestCache}
import org.sailcbi.APIServer.CbiUtil.{NetFailure, NetSuccess, ParsedRequest}
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.PaymentMethod
import org.sailcbi.APIServer.Entities.MagicIds.ORDER_NUMBER_APP_ALIAS
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.ProtoPersonRequestCache
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ClearCard @Inject()(ws: WSClient)(implicit val exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, ClearCardShape.apply)(parsed => {
			parsed.program match {
				case ORDER_NUMBER_APP_ALIAS.AP | ORDER_NUMBER_APP_ALIAS.JP => PA.withRequestCacheMember(parsedRequest, rc => {
					val personId = rc.getAuthedPersonId()
					postInner(rc, personId, parsed.program)
				})
				case _ => PA.withRequestCache(ProtoPersonRequestCache)(None, parsedRequest, rc => {
					val personId = rc.getAuthedPersonId().get
					postInner(rc, personId, parsed.program)
				})
			}
		})
	}

	private def postInner(rc: RequestCache, personId: Int, program: String): Future[Result] = {
		val stripeIOController = rc.getStripeIOController(ws)

		val orderId = PortalLogic.getOrderId(rc, personId, program)

		if (PortalLogic.getPaymentAdditionalMonths(rc, orderId) > 0) {
			val stripeCustomerId = PortalLogic.getStripeCustomerId(rc, personId)
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
	}

	case class ClearCardShape(program: String)
	object ClearCardShape {
		implicit val format = Json.format[ClearCardShape]

		def apply(v: JsValue): ClearCardShape = v.as[ClearCardShape]
	}
}