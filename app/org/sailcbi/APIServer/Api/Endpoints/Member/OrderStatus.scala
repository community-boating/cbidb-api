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
			val rc: RequestCache = PA.getRequestCacheMember(None, parsedRequest)._2.get
			val pb: PersistenceBroker = rc.pb

			val personId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)
			val orderId = JPPortal.getOrderId(pb, personId)

			val cardDataQ = new PreparedQueryForSelect[StripeTokenSavedShape](Set(MemberUserType)) {
				override val params: List[String] = List(orderId.toString)

				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): StripeTokenSavedShape = new StripeTokenSavedShape(
					token = rsw.getString(2),
					orderId = rsw.getInt(1),
					last4 = rsw.getString(3),
					expMonth = rsw.getInt(4).toString,
					expYear = rsw.getInt(5).toString,
					zip = rsw.getOptionString(6)
				)

				override def getQuery: String =
					"""
					  |select ORDER_ID, TOKEN, CARD_LAST_DIGITS, CARD_EXP_MONTH, CARD_EXP_YEAR, CARD_ZIP
					  |from active_stripe_tokens where order_id = ?
					  |""".stripMargin
			}
			val cardData = pb.executePreparedQueryForSelect(cardDataQ).headOption

			val orderTotalQ = new PreparedQueryForSelect[Double](Set(MemberUserType)) {
				override val params: List[String] = List(orderId.toString)

				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Double = rsw.getDouble(1)

				override def getQuery: String = "select cc_pkg.calculate_total(?) from dual"
			}
			val orderTotal = pb.executePreparedQueryForSelect(orderTotalQ).head
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
