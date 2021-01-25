package org.sailcbi.APIServer.Api.Endpoints.Member

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApSelectMemForPurchase @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(None, parsedRequest, rc => {
			val personId = rc.getAuthedPersonId(rc)
			PA.withParsedPostBodyJSON(request.body.asJson, ApSelectMemForPurchaseShape.apply)(parsed => {
				val orderId = PortalLogic.getOrderIdAP(rc, personId)
				// TODO: check mem type is valid

				val discountInstanceId = parsed.requestedDiscountId.map(PortalLogic.getDiscountActiveInstanceForDiscount(rc, _))
				val (paymentPlanAllowed, guestPrivsAuto, guestPrivsNA, dmgWaiverAuto) = PortalLogic.apSelectMemForPurchase(rc, personId, orderId, parsed.memTypeId, discountInstanceId)
				val now = PA.now().toLocalDate

				PortalLogic.assessDiscounts(rc, orderId)
				PortalLogic.writeOrderStaggeredPayments(rc, now, personId, orderId, 0)


				Future(Ok(new JsObject(Map(
					"paymentPlanAllowed" -> JsBoolean(paymentPlanAllowed),
					"guestPrivsAuto" -> JsBoolean(guestPrivsAuto),
					"guestPrivsNA" -> JsBoolean(guestPrivsNA),
					"damageWavierAuto" -> JsBoolean(dmgWaiverAuto),
				))))
			})
		})
	}

	case class ApSelectMemForPurchaseShape (
		memTypeId: Int,
		requestedDiscountId: Option[Int]
	)

	object ApSelectMemForPurchaseShape {
		implicit val format = Json.format[ApSelectMemForPurchaseShape]

		def apply(v: JsValue): ApSelectMemForPurchaseShape = v.as[ApSelectMemForPurchaseShape]
	}
}
