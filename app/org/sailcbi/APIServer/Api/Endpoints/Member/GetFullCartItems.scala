package org.sailcbi.APIServer.Api.Endpoints.Member

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.IO.PreparedQueries.Member.FullCart
import org.sailcbi.APIServer.Services.{CacheBroker, PermissionsAuthority}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetFullCartItems @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get(program: String)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(None, parsedRequest, rc => {
			val pb = rc.pb
			val cb: CacheBroker = rc.cb
			val personId = rc.auth.getAuthedPersonId(rc)
			val orderId = PortalLogic.getOrderId(rc, personId, program)

			val fullCartItemsQuery = new FullCart(orderId)

			val resultObj = rc.executePreparedQueryForSelect(fullCartItemsQuery)
			Future(Ok(Json.toJson(resultObj)))
		})
	}
}
