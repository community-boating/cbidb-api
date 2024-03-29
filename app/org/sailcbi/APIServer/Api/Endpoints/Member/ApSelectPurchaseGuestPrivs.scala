package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.MemberRequestCache
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApSelectPurchaseGuestPrivs @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val logger = PA.logger
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
			val personId = rc.getAuthedPersonId
			val orderId = PortalLogic.getOrderIdAP(rc, personId)

			val q = new PreparedQueryForSelect[Int](Set(MemberRequestCache)) {
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

			val exists = rc.executePreparedQueryForSelect(q).head > 0

			Future(Ok(new JsObject(Map(
				"wantIt" -> JsBoolean(exists)
			))))
		})
	}
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val logger = PA.logger
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(request.body.asJson, ApSelectPurchaseGuestPrivsShape.apply)(parsed => {
			PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {

				val personId = rc.getAuthedPersonId
				val orderId = PortalLogic.getOrderIdAP(rc, personId)

				PortalLogic.apSetGuestPrivs(rc, personId, orderId, parsed.wantIt)

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
