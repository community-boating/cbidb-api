package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.MemberRequestCache
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class JpSetMembershipPaymentCount @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val logger = PA.logger
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(request.body.asJson, JpSetMembershipPaymentCount.apply)(parsed => {
			PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
				val personId = rc.getAuthedPersonId
				val orderId = PortalLogic.getOrderIdJP(rc, personId)

				val now = PA.now().toLocalDate

				PortalLogic.writeOrderStaggeredPaymentsJP(rc, now, orderId, parsed.doStaggeredPayments)
				PortalLogic.clearStripeTokensFromOrder(rc, orderId)
				PortalLogic.unapplyAllGCFromOrder(rc, orderId)

				Future(Ok(new JsObject(Map(
					"success" -> JsBoolean(true)
				))))
			})
		})
	}

	case class JpSetMembershipPaymentCount (
		doStaggeredPayments: Boolean
	)

	object JpSetMembershipPaymentCount {
		implicit val format = Json.format[JpSetMembershipPaymentCount]

		def apply(v: JsValue): JpSetMembershipPaymentCount = v.as[JpSetMembershipPaymentCount]
	}
}