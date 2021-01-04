package org.sailcbi.APIServer.Api.Endpoints.Member

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.IO.Portal.PortalLogic.{SignupForReport, WaitListForReport, WaitListTopForReport}
import org.sailcbi.APIServer.Services.{CacheBroker, PermissionsAuthority}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetAllJuniorClassSignups  @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def get(juniorId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCacheMemberWithJuniorId(None, parsedRequest, juniorId, rc => {
			val pb = rc.pb
			val cb: CacheBroker = rc.cb

			implicit val format = AllSignupsResponse.format
			val signups: AllSignupsResponse = AllSignupsResponse(
				juniorId,
				PortalLogic.getSignupsForReport(rc, juniorId),
				PortalLogic.getWaitListTopsForReport(rc, juniorId),
				PortalLogic.getWaitListsForReport(rc, juniorId)
			)


			val resultJson: JsValue = Json.toJson(signups)
			Future(Ok(resultJson))
		})
	})

	case class AllSignupsResponse(juniorId: Int, enrollments: List[SignupForReport], waitListTops: List[WaitListTopForReport], waitLists: List[WaitListForReport])

	object AllSignupsResponse {

		implicit val format = Json.format[AllSignupsResponse]

		def apply(v: JsValue): AllSignupsResponse = v.as[AllSignupsResponse]
	}
}
