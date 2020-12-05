package org.sailcbi.APIServer.Api.Endpoints.Member

import java.time.LocalDate

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.{NetFailure, NetSuccess, ParsedRequest, ServiceRequestResult}
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.{Charge, Customer, PaymentIntent, PaymentMethod, StripeError}
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
			val stripe = rc.getStripeIOController(ws)

			val personId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)
			val orderId = PortalLogic.getOrderId(pb, personId)

			val orderTotal = PortalLogic.getOrderTotal(pb, orderId)

			val staggeredPaymentAdditionalMonths = PortalLogic.getPaymentAdditionalMonths(pb, orderId)

			val now = PA.now().toLocalDate

			implicit val format = OrderStatusResult.format

			if (staggeredPaymentAdditionalMonths > 0) {
				val customerId = PortalLogic.getStripeCustomerId(pb, personId).get

				stripe.getCustomerDefaultPaymentMethod(customerId).flatMap({
					case methodSuccess: NetSuccess[Option[PaymentMethod], StripeError] => PortalLogic.getOrCreatePaymentIntent(pb, stripe, personId, orderId, (orderTotal * 100).toInt).map(pi => {
						Ok(Json.toJson(OrderStatusResult(
							orderId = orderId,
							total = orderTotal,
							paymentMethodRequired = true,
							cardData = (methodSuccess.successObject.asInstanceOf[Option[PaymentMethod]]).map(pm => SavedCardOrPaymentMethodData(
								last4 = pm.card.last4,
								expMonth = pm.card.exp_month,
								expYear = pm.card.exp_year,
								zip = pm.billing_details.address.postal_code
							)),
							staggeredPayments = PortalLogic.writeOrderStaggeredPayments(pb, now, personId, orderId, staggeredPaymentAdditionalMonths).map(Function.tupled(
								(ld, amt) => StaggeredPayment(ld, amt.cents)
							)),
							paymentIntentId = Some(pi.id)
						)))
					})
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
					staggeredPayments = List.empty,
					paymentIntentId = None
				))))
			}
		})
	}

	case class StaggeredPayment(
		paymentDate: LocalDate,
		paymentAmtCents: Int
	)

	object StaggeredPayment {
		implicit val format = Json.format[StaggeredPayment]
		def apply(v: JsValue): StaggeredPayment = v.as[StaggeredPayment]
	}

	case class OrderStatusResult(
		orderId: Int,
		total: Double,
		paymentMethodRequired: Boolean,
		cardData: Option[SavedCardOrPaymentMethodData],
		staggeredPayments: List[StaggeredPayment],
		paymentIntentId: Option[String]
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
