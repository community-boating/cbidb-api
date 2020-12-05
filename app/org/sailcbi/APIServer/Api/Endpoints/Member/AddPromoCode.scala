package org.sailcbi.APIServer.Api.Endpoints.Member

import org.sailcbi.APIServer.Api.ValidationResult

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker}
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.{ExecutionContext, Future}

class AddPromoCode @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(None, parsedRequest, rc => {
			val pb: PersistenceBroker = rc.pb
			val personId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)
			val orderId = PortalLogic.getOrderId(pb, personId)
			val now = PA.now().toLocalDate
			PA.withParsedPostBodyJSON(request.body.asJson, AddPromoCodeShape.apply)(parsed => {
				parsed.promoCode match {
					case None =>Future(Ok(ValidationResult.from("Promo code must be speficied.").toResultError.asJsObject()))
					case Some(promoCode) => {
						PortalLogic.attemptAddPromoCode(pb, orderId, promoCode)
						val staggeredPaymentAdditionalMonths = PortalLogic.getPaymentAdditionalMonths(pb, orderId)
						PortalLogic.assessDiscounts(pb, orderId)
						PortalLogic.writeOrderStaggeredPayments(pb, now, personId, orderId, staggeredPaymentAdditionalMonths)

						Future(Ok(JsObject(Map("Success" -> JsBoolean(true)))))
					}
				}

			})
		})
	}

	case class AddPromoCodeShape(
		promoCode: Option[String]
	)

	object AddPromoCodeShape {
		implicit val format = Json.format[AddPromoCodeShape]

		def apply(v: JsValue): AddPromoCodeShape = v.as[AddPromoCodeShape]
	}
}
