package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Entities.Misc.StripeTokenSavedShape
import org.sailcbi.APIServer.IO.Junior.JPPortal
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.{ExecutionContext, Future}

class OrderStatus @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(None, parsedRequest, rc => {
			val pb: PersistenceBroker = rc.pb

			val personId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)
			val orderId = JPPortal.getOrderId(pb, personId)

			val orderTotal = JPPortal.getOrderTotal(pb, orderId)

			val cardData = JPPortal.getCardData(pb, orderId)

			val result = OrderStatusResult(
				orderId = orderId,
				total = orderTotal,
				cardData = cardData
			)

			implicit val format = OrderStatusResult.format
			Future(Ok(Json.toJson(result)))
		})
	}

	case class OrderStatusResult(orderId: Int, total: Double, cardData: Option[StripeTokenSavedShape])

	object OrderStatusResult {
		implicit val format = Json.format[OrderStatusResult]

		def apply(v: JsValue): OrderStatusResult = v.as[OrderStatusResult]
	}
}
