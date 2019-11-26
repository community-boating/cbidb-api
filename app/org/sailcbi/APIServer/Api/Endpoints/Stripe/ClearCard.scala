package org.sailcbi.APIServer.Api.Endpoints.Stripe

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Junior.JPPortal
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForUpdateOrDelete
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker}
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.{ExecutionContext, Future}

class ClearCard @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(None, parsedRequest, rc => {
			val pb: PersistenceBroker = rc.pb

			val personId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)
			val orderId = JPPortal.getOrderId(pb, personId)

			val clearQ = new PreparedQueryForUpdateOrDelete(Set(MemberUserType)) {
				override val params: List[String] = List(orderId.toString)

				override def getQuery: String = "update stripe_tokens set active = 'N' where order_id = ?"
			}
			pb.executePreparedQueryForUpdateOrDelete(clearQ)
			Future(Ok("done"))
		})
	}
}