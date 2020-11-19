package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker}
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.{ExecutionContext, Future}

class ApSelectMemForPurchase @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(None, parsedRequest, rc => {
			val pb: PersistenceBroker = rc.pb
			val personId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)
			PA.withParsedPostBodyJSON(request.body.asJson, ApSelectMemForPurchaseShape.apply)(parsed => {
				val orderId = PortalLogic.getOrderId(pb, personId)
				// TODO: check mem type is valid

				val discountInstanceId = parsed.requestedDiscountId.map(PortalLogic.getDiscountActiveInstanceForDiscount(pb, _))
				val (paymentPlanAllowed, guestPrivsAuto, guestPrivsNA, dmgWaiverAuto) = PortalLogic.apSelectMemForPurchase(pb, personId, orderId, parsed.memTypeId, discountInstanceId)
				val now = PA.now().toLocalDate
				PortalLogic.writeOrderStaggeredPayments(pb, now, personId, orderId, 0)

				PortalLogic.assessDiscounts(pb, orderId)

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
