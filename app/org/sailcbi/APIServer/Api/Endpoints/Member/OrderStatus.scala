package org.sailcbi.APIServer.Api.Endpoints.Member

import org.sailcbi.APIServer.CbiUtil.{Currency, NetFailure, NetSuccess, ParsedRequest}
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.{PaymentMethod, StripeError}
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class OrderStatus @Inject()(ws: WSClient)(implicit val exec: ExecutionContext) extends InjectedController {
	def get(program: String)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(parsedRequest, rc => {
			val stripe = rc.getStripeIOController(ws)

			val personId = rc.getAuthedPersonId()
			val orderId = PortalLogic.getOrderId(rc, personId, program)

			val orderTotal = PortalLogic.getOrderTotalDollars(rc, orderId)
			val orderTotalInCents = Currency.toCents(orderTotal)

			val staggeredPaymentAdditionalMonths = PortalLogic.getPaymentAdditionalMonths(rc, orderId)

			val destructurePaymentPlan = Function.tupled(
				(ld: LocalDate, amt: Currency) => StaggeredPayment(ld, amt.cents)
			)

			val now = PA.now().toLocalDate

			val staggeredPayments = if (staggeredPaymentAdditionalMonths == 0) {
				List.empty
			} else if (program == "JP") {
				PortalLogic.writeOrderStaggeredPaymentsJP(rc, now, orderId, staggeredPaymentAdditionalMonths > 0).map(destructurePaymentPlan)
			} else if (program == "AP") {
				PortalLogic.writeOrderStaggeredPaymentsAP(rc, now, personId, orderId, staggeredPaymentAdditionalMonths).map(destructurePaymentPlan)
			} else throw new Exception("Unrecognized program " + program)

			val jpPotentialStaggeredPayments =
				if (program == "JP") PortalLogic.getJPAvailablePaymentSchedule(rc, orderId, now, true).map(destructurePaymentPlan)
				else List.empty

			implicit val format = OrderStatusResult.format

			if (staggeredPaymentAdditionalMonths > 0) {
				val customerId = PortalLogic.getStripeCustomerId(rc, personId).get

				stripe.getCustomerDefaultPaymentMethod(customerId).flatMap({
					case methodSuccess: NetSuccess[Option[PaymentMethod], StripeError] => PortalLogic.getOrCreatePaymentIntent(rc, stripe, personId, orderId, orderTotalInCents).map(pi => {
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
							staggeredPayments = staggeredPayments,
							paymentIntentId = pi.map(_.id),
							jpAvailablePaymentSchedule = jpPotentialStaggeredPayments,
						)))
					})
					case _: NetFailure[_, StripeError] => throw new Exception("Failed to get default payment method for customer " + customerId)
				})
			} else {
				val cardData = PortalLogic.getCardData(rc, orderId)
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
					paymentIntentId = None,
					jpAvailablePaymentSchedule = jpPotentialStaggeredPayments,
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
		paymentIntentId: Option[String],
		jpAvailablePaymentSchedule: List[StaggeredPayment]
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
