package org.sailcbi.APIServer.Api.Endpoints.Member

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
			PA.withParsedPostBodyJSON(request.body.asJson, AddPromoCodeShape.apply)(parsed => {
				PortalLogic.attemptAddPromoCode(pb, orderId, parsed.promoCode)
				PortalLogic.assessDiscounts(pb, orderId)

				Future(Ok(JsObject(Map("Success" -> JsBoolean(true)))))
			})
		})
	}

	case class AddPromoCodeShape(
		promoCode: String
	)

	object AddPromoCodeShape {
		implicit val format = Json.format[AddPromoCodeShape]

		def apply(v: JsValue): AddPromoCodeShape = v.as[AddPromoCodeShape]
	}
}
