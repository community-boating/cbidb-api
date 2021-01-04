package org.sailcbi.APIServer.Api.Endpoints.Member

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, ResultSetWrapper}
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApSelectPurchaseGuestPrivs @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val logger = PA.logger
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(None, parsedRequest, rc => {
			val pb = rc.pb
			val personId = rc.auth.getAuthedPersonId(pb)
			val orderId = PortalLogic.getOrderId(pb, personId)

			val q = new PreparedQueryForSelect[Int](Set(MemberUserType)) {
				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

				override val params: List[String] = List(
					personId.toString,
					orderId.toString,
					orderId.toString
				)

				override def getQuery: String =
					"""
					  |select count(*) from shopping_cart_guest_privs
					  |where sc_membership_id in (select item_id from shopping_cart_memberships where person_id = ? and order_id = ?)
					  |and order_id = ?
					  |""".stripMargin
			}

			val exists = pb.executePreparedQueryForSelect(q).head > 0

			Future(Ok(new JsObject(Map(
				"wantIt" -> JsBoolean(exists)
			))))
		})
	}
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val logger = PA.logger
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(request.body.asJson, ApSelectPurchaseGuestPrivsShape.apply)(parsed => {
			PA.withRequestCacheMember(None, parsedRequest, rc => {
				val pb = rc.pb
				val personId = rc.auth.getAuthedPersonId(pb)
				val orderId = PortalLogic.getOrderId(pb, personId)

				PortalLogic.apSetGuestPrivs(pb, personId, orderId, parsed.wantIt)

				Future(Ok(new JsObject(Map(
					"success" -> JsBoolean(true)
				))))
			})
		})
	}

	case class ApSelectPurchaseGuestPrivsShape (
		wantIt: Boolean
	)

	object ApSelectPurchaseGuestPrivsShape {
		implicit val format = Json.format[ApSelectPurchaseGuestPrivsShape]

		def apply(v: JsValue): ApSelectPurchaseGuestPrivsShape = v.as[ApSelectPurchaseGuestPrivsShape]
	}
}
