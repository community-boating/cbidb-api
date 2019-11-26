package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Junior.JPPortal
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForUpdateOrDelete
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.{ExecutionContext, Future}

class AcceptTOS  @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val logger = PA.logger
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(request.body.asJson, AcceptTOSShape.apply)(parsed => {
			PA.withRequestCacheMemberWithJuniorId(None, parsedRequest, parsed.personId, rc => {
				val pb = rc.pb

				val parentId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)
				val orderId = JPPortal.getOrderId(pb, parentId)

				val q = new PreparedQueryForUpdateOrDelete(Set(MemberUserType)) {
					override val params: List[String] = List(parsed.personId.toString, orderId.toString)

					override def getQuery: String =
						"""
						  |update shopping_cart_memberships
						  |  set ready_to_buy = 'Y'
						  |  where person_id = ?
						  |  and ready_to_buy = 'N'
						  |  and order_id = ?
						  |""".stripMargin
				}
				pb.executePreparedQueryForUpdateOrDelete(q)
				Future(Ok(JsObject(Map("Success" -> JsBoolean(true)))))
			})
		})
	}

	case class AcceptTOSShape(personId: Int)

	object AcceptTOSShape {
		implicit val format = Json.format[AcceptTOSShape]

		def apply(v: JsValue): AcceptTOSShape = v.as[AcceptTOSShape]
	}

}
