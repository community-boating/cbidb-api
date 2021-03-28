package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.framework.Core.{ParsedRequest, PermissionsAuthority, RequestCache}
import com.coleji.framework.IO.PreparedQueries.PreparedQueryForUpdateOrDelete
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.MemberRequestCache
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AcceptTOS  @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val logger = PA.logger
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(request.body.asJson, AcceptTOSShape.apply)(parsed => {
			PA.withRequestCacheMemberWithJuniorId(parsedRequest, parsed.personId, rc => {
				val parentId = rc.getAuthedPersonId()
				val orderId = PortalLogic.getOrderIdJP(rc, parentId)

				doAccept(rc, orderId, parsed.personId)
			})
		})
	}

	def postAP()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val logger = PA.logger
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(parsedRequest, rc => {

			val personId = rc.getAuthedPersonId()
			val orderId = PortalLogic.getOrderIdAP(rc, personId)

			doAccept(rc, orderId, personId)
		})
	}

	private def doAccept(rc: RequestCache, orderId: Int, personId: Int): Future[Result] = {
		val q = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
			override val params: List[String] = List(personId.toString, orderId.toString)

			override def getQuery: String =
				"""
				  |update shopping_cart_memberships
				  |  set ready_to_buy = 'Y'
				  |  where person_id = ?
				  |  and ready_to_buy = 'N'
				  |  and order_id = ?
				  |""".stripMargin
		}
		rc.executePreparedQueryForUpdateOrDelete(q)
		Future(Ok(JsObject(Map("Success" -> JsBoolean(true)))))
	}

	case class AcceptTOSShape(personId: Int)

	object AcceptTOSShape {
		implicit val format = Json.format[AcceptTOSShape]

		def apply(v: JsValue): AcceptTOSShape = v.as[AcceptTOSShape]
	}

}
