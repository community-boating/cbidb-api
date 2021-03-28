package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.framework.Core.{PermissionsAuthority, RequestCache}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
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
	def postJP(juniorId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCacheMemberWithJuniorId(parsedRequest, juniorId, rc => {
			val parentPersonId = rc.getAuthedPersonId()
			post(rc, juniorId, Some(parentPersonId))
		})
	})

	private def post(rc: RequestCache, personId: Int, parentPersonId: Option[Int] = None): Future[Result] = {
		PortalLogic.getOpenStaggeredOrderForPerson(rc, personId) match {
			case None => Future(Ok("success"))
			case Some(orderId) => {
				PortalLogic.finishOpenOrder(rc, parentPersonId.getOrElse(personId), orderId)
				Future(Ok("success"))
			}
		}
	}
}
