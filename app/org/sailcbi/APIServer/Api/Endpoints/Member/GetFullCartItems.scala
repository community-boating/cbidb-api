package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Junior.JPPortal
import org.sailcbi.APIServer.IO.PreparedQueries.Member.FullCart
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.Exception.UnauthorizedAccessException
import org.sailcbi.APIServer.Services.{CacheBroker, PermissionsAuthority, PersistenceBroker, RequestCache}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.ExecutionContext

class GetFullCartItems @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action { request =>
		try {
			val parsedRequest = ParsedRequest(request)
			val rc: RequestCache = PA.getRequestCacheMember(None, parsedRequest).get
			val pb: PersistenceBroker = rc.pb
			val cb: CacheBroker = rc.cb
			val personId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)
			val orderId = JPPortal.getOrderId(pb, personId)

			val fullCartItemsQuery = new FullCart(orderId)

			val resultObj = pb.executePreparedQueryForSelect(fullCartItemsQuery)
			Ok(Json.toJson(resultObj))
		} catch {
			case e: UnauthorizedAccessException => {
				e.printStackTrace()
				Ok("Access Denied")
			}
			case e: Throwable => {
				println(e)
				Ok("Internal Error")
			}
		}

	}

}