package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.Api.AuthenticatedRequest
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Entities.Misc.StripeTokenSavedShape
import org.sailcbi.APIServer.IO.Junior.JPPortal
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.{CacheBroker, PermissionsAuthority, PersistenceBroker, RequestCache, ResultSetWrapper}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext

class OrderStatus @Inject()(implicit val exec: ExecutionContext) extends AuthenticatedRequest {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action { request =>
		val parsedRequest = ParsedRequest(request)
		try {
			val rc: RequestCache = PA.getRequestCacheMember(None, parsedRequest).get
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
			Ok(Json.toJson(result))
		} catch {
			case _: Throwable => Ok("Internal Error.")
		}

	}

	case class OrderStatusResult(orderId: Int, total: Double, cardData: Option[StripeTokenSavedShape])

	object OrderStatusResult {
		implicit val format = Json.format[OrderStatusResult]

		def apply(v: JsValue): OrderStatusResult = v.as[OrderStatusResult]
	}
}
