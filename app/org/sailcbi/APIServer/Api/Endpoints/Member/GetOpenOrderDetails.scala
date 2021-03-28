package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.framework.Core.{ParsedRequest, PermissionsAuthority, RequestCache}
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetOpenOrderDetails @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def getSelf()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCacheMember(parsedRequest, rc => {
			val personId = rc.getAuthedPersonId()
			get(rc, personId)
		})
	})
	def getJunior(juniorId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCacheMemberWithJuniorId(parsedRequest, juniorId, rc => get(rc, juniorId))
	})
	private def get(rc: RequestCache, personId: Int): Future[Result] = {
		implicit val format = OpenOrderDetailsResult.format

		PortalLogic.getOpenStaggeredOrderForPerson(rc, personId) match {
			case None => Future(Ok(Json.toJson(List.empty)))
			case Some(orderId) => Future(Ok(Json.toJson(PortalLogic.getStaggeredOrderStatus(rc, orderId))))
		}

	}

	case class OpenOrderDetailsResult(orderId: Int)
	object OpenOrderDetailsResult {
		implicit val format = Json.format[OpenOrderDetailsResult]
		def apply(v: JsValue): OpenOrderDetailsResult = v.as[OpenOrderDetailsResult]
	}
}