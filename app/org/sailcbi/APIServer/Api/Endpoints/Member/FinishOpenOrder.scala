package org.sailcbi.APIServer.Api.Endpoints.Member

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Services.{PermissionsAuthority, RequestCache}
import play.api.mvc.{Action, AnyContent, InjectedController, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FinishOpenOrder @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def postSelf()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCacheMember(parsedRequest, rc => {
			val personId = rc.getAuthedPersonId()
			post(rc, personId)
		})
	})

	private def post(rc: RequestCache, personId: Int): Future[Result] = {
		PortalLogic.getOpenStaggeredOrderForPerson(rc, personId) match {
			case None => Future(Ok("success"))
			case Some(orderId) => {
				PortalLogic.finishOpenOrder(rc, personId, orderId)
				Future(Ok("success"))
			}
		}
	}
}
