package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.{GetSQLLiteralPrepared, ParsedRequest}
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.IO.PreparedQueries.{PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker, ResultSetWrapper}
import play.api.libs.json.{JsBoolean, JsNumber, JsObject, JsValue, Json}
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
				PortalLogic.apSelectMemForPurchase(pb, personId, orderId, 1, None)

				PortalLogic.assessDiscounts(pb, orderId)

				Future(Ok(new JsObject(Map(
					"success" -> JsBoolean(true)
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