package org.sailcbi.APIServer.Api.Endpoints.Stripe

import javax.inject.Inject
import org.sailcbi.APIServer.Api.AuthenticatedRequest
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Entities.Misc.StripeTokenSavedShape
import org.sailcbi.APIServer.IO.Junior.JPPortal
import org.sailcbi.APIServer.IO.PreparedQueries.{PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker, RequestCache, ResultSetWrapper}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext

class ClearCard @Inject()(implicit val exec: ExecutionContext) extends AuthenticatedRequest {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action { request =>
		val parsedRequest = ParsedRequest(request)
		try {
			val rc: RequestCache = PA.getRequestCacheMember(None, parsedRequest).get
			val pb: PersistenceBroker = rc.pb

			val personId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)
			val orderId = JPPortal.getOrderId(pb, personId)

			val clearQ = new PreparedQueryForUpdateOrDelete(Set(MemberUserType)) {
				override val params: List[String] = List(orderId.toString)

				override def getQuery: String = "update stripe_tokens set active = 'N' where order_id = ?"
			}
			pb.executePreparedQueryForUpdateOrDelete(clearQ)
			Ok("done")
		} catch {
			case _: Throwable => Ok("Internal Error.")
		}

	}
}