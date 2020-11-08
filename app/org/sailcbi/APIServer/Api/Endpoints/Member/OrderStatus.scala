package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.{NetFailure, NetSuccess, ParsedRequest, ServiceRequestResult}
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.{Charge, Customer, PaymentMethod, StripeError}
import org.sailcbi.APIServer.Entities.Misc.StripeTokenSavedShape
import org.sailcbi.APIServer.IO.HTTP.POST
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker}
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.{ExecutionContext, Future}

class OrderStatus @Inject()(ws: WSClient)(implicit val exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(None, parsedRequest, rc => {
			val pb: PersistenceBroker = rc.pb

			val personId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)
			val orderId = PortalLogic.getOrderId(pb, personId)

			val orderTotal = PortalLogic.getOrderTotal(pb, orderId)

			val staggeredPaymentAdditionalMonths = PortalLogic.getPaymentAdditionalMonths(pb, orderId)

			implicit val format = OrderStatusResult.format

			if (staggeredPaymentAdditionalMonths > 0) {
				val customerId = PortalLogic.getStripeCustomerId(pb, personId).get
				rc.getStripeIOController(ws).getCustomerDefaultPaymentMethod(customerId).map({
					case c: NetSuccess[PaymentMethod, StripeError] => Ok(Json.toJson(OrderStatusResult(
						orderId = orderId,
						total = orderTotal,
						paymentMethodRequired = true,
						cardData = Some(SavedCardOrPaymentMethodData(
							last4 = c.successObject.card.last4,
							expMonth = c.successObject.card.exp_month,
							expYear = c.successObject.card.exp_year,
							zip = c.successObject.billing_details.address.postal_code
						))
					)))
					case _: NetFailure[_, StripeError] => throw new Exception("Failed to get default payment method for customer " + customerId)
				})


			} else {
				val cardData = PortalLogic.getCardData(pb, orderId)
				Future(Ok(Json.toJson(OrderStatusResult(
					orderId = orderId,
					total = orderTotal,
					paymentMethodRequired = false,
					cardData = cardData.map(cd => SavedCardOrPaymentMethodData(
						last4 = cd.last4,
						expMonth = cd.expMonth.toInt,
						expYear = cd.expYear.toInt,
						zip = cd.zip
					)),
				))))
			}
		})
	}

	case class OrderStatusResult(
		orderId: Int,
		total: Double,
		paymentMethodRequired: Boolean,
		cardData: Option[SavedCardOrPaymentMethodData],
	)

	object OrderStatusResult {
		implicit val PaymentMethodForBrowserFormat = SavedCardOrPaymentMethodData.format
		implicit val format = Json.format[OrderStatusResult]
		def apply(v: JsValue): OrderStatusResult = v.as[OrderStatusResult]
	}

	case class SavedCardOrPaymentMethodData(
		last4: String,
		expMonth: Int,
		expYear: Int,
		zip: Option[String]
	)

	object SavedCardOrPaymentMethodData {
		implicit val format = Json.format[SavedCardOrPaymentMethodData]
		def apply(v: JsValue): SavedCardOrPaymentMethodData = v.as[SavedCardOrPaymentMethodData]
	}
}
