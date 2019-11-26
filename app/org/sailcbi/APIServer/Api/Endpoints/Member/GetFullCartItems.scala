package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Junior.JPPortal
import org.sailcbi.APIServer.IO.PreparedQueries.Member.FullCart
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.{CacheBroker, PermissionsAuthority, PersistenceBroker}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.{ExecutionContext, Future}

class GetFullCartItems @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(None, parsedRequest, rc => {
			val pb: PersistenceBroker = rc.pb
			val cb: CacheBroker = rc.cb
			val personId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)
			val orderId = JPPortal.getOrderId(pb, personId)

			val fullCartItemsQuery = new FullCart(orderId)

			val resultObj = pb.executePreparedQueryForSelect(fullCartItemsQuery)
			Future(Ok(Json.toJson(resultObj)))
		})
	}
}
