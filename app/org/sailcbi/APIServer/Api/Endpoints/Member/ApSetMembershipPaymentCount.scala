package org.sailcbi.APIServer.Api.Endpoints.Member

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApSetMembershipPaymentCount @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val logger = PA.logger
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(request.body.asJson, ApSetMembershipPaymentCount.apply)(parsed => {
			PA.withRequestCacheMember(parsedRequest, rc => {
				val personId = rc.getAuthedPersonId(rc)
				val orderId = PortalLogic.getOrderIdAP(rc, personId)

				val now = PA.now().toLocalDate

				PortalLogic.writeOrderStaggeredPayments(rc, now, personId, orderId, parsed.additionalPayments)
				PortalLogic.clearStripeTokensFromOrder(rc, orderId)

				Future(Ok(new JsObject(Map(
					"success" -> JsBoolean(true)
				))))
			})
		})
	}

	case class ApSetMembershipPaymentCount (
		additionalPayments: Int
	)

	object ApSetMembershipPaymentCount {
		implicit val format = Json.format[ApSetMembershipPaymentCount]

		def apply(v: JsValue): ApSetMembershipPaymentCount = v.as[ApSetMembershipPaymentCount]
	}
}