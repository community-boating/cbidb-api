package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.neptune.API.ValidationResult
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.MemberRequestCache
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddPromoCode @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
			val personId = rc.getAuthedPersonId
			val now = PA.now().toLocalDate
			PA.withParsedPostBodyJSON(request.body.asJson, AddPromoCodeShape.apply)(parsed => {
				val orderId = PortalLogic.getOrderId(rc, personId, parsed.program)
				parsed.promoCode match {
					case None =>Future(Ok(ValidationResult.from("Promo code must be specified.").toResultError.asJsObject()))
					case Some(promoCode) => {
						PortalLogic.attemptAddPromoCode(rc, orderId, promoCode)
						val staggeredPaymentAdditionalMonths = PortalLogic.getPaymentAdditionalMonths(rc, orderId)
						PortalLogic.assessDiscounts(rc, orderId)
						PortalLogic.writeOrderStaggeredPaymentsAP(rc, now, personId, orderId, staggeredPaymentAdditionalMonths)

						Future(Ok(JsObject(Map("Success" -> JsBoolean(true)))))
					}
				}

			})
		})
	}

	case class AddPromoCodeShape(
		program: String,
		promoCode: Option[String]
	)

	object AddPromoCodeShape {
		implicit val format = Json.format[AddPromoCodeShape]

		def apply(v: JsValue): AddPromoCodeShape = v.as[AddPromoCodeShape]
	}
}
