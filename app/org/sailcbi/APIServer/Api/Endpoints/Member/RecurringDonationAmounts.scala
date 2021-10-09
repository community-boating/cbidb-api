package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import com.coleji.neptune.Util.NetSuccess
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.{PaymentMethod, StripeError}
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.IO.Portal.PortalLogic.{RecurringDonation, SavedCardOrPaymentMethodData}
import org.sailcbi.APIServer.UserTypes.MemberRequestCache
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RecurringDonationAmounts @Inject()(ws: WSClient)(implicit exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
			val personId = rc.getAuthedPersonId
			val customerId = PortalLogic.getStripeCustomerId(rc, personId).get
			val stripe = rc.getStripeIOController(ws)
			val donations = PortalLogic.getRecurringDonations(rc, personId)

			stripe.getCustomerDefaultPaymentMethod(customerId).map({
				case s: NetSuccess[Option[PaymentMethod], StripeError] => {
					val cardDataMaybe = s.successObject.map(pm => SavedCardOrPaymentMethodData(
						last4 = pm.card.last4,
						expMonth = pm.card.exp_month,
						expYear = pm.card.exp_year,
						zip = pm.billing_details.address.postal_code
					))
					implicit val format = GetRecurringDonationsShape.format
					val resultJson: JsValue = Json.toJson(GetRecurringDonationsShape(
						recurringDonations=donations,
						paymentMethod=cardDataMaybe
					))

					Ok(resultJson)
				}
				case _ => {
					implicit val format = GetRecurringDonationsShape.format
					val resultJson: JsValue = Json.toJson(GetRecurringDonationsShape(
						recurringDonations=donations,
						paymentMethod=None
					))

					Ok(resultJson)
				}
			})
		})
	}

	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
			PA.withParsedPostBodyJSON(parsedRequest.postJSON, GetRecurringDonationsShape.apply)(parsed => {
				val personId = rc.getAuthedPersonId

				val q = new PreparedQueryForSelect[Option[LocalDate]](Set(MemberRequestCache)) {
					override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Option[LocalDate] = rsw.getOptionLocalDate(1)

					override def getQuery: String = s"select NEXT_RECURRING_DONATION from persons where person_id = $personId"
				}
				val nextDonation = rc.executePreparedQueryForSelect(q).head

				PortalLogic.setRecurringDonations(rc, personId, parsed.recurringDonations, nextDonation.isEmpty)
				Future(Ok(new JsObject(Map(
					"success" -> JsBoolean(true)
				))))
			})
		})
	}

	case class GetRecurringDonationsShape (recurringDonations: List[RecurringDonation], paymentMethod: Option[SavedCardOrPaymentMethodData])
	object GetRecurringDonationsShape {
		implicit val format = Json.format[GetRecurringDonationsShape]
		def apply(v: JsValue): GetRecurringDonationsShape = v.as[GetRecurringDonationsShape]
	}
}
