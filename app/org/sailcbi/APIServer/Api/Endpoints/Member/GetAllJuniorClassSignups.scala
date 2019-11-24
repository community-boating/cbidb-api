package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Junior.JPPortal
import org.sailcbi.APIServer.IO.Junior.JPPortal.{SignupForReport, WaitListForReport, WaitListTopForReport}
import org.sailcbi.APIServer.Services.Exception.UnauthorizedAccessException
import org.sailcbi.APIServer.Services.{CacheBroker, PermissionsAuthority, PersistenceBroker, RequestCache}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.ExecutionContext

class GetAllJuniorClassSignups  @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def get(juniorId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action(req => {
		try {
			val parsedRequest = ParsedRequest(req)
			val rc: RequestCache = PA.getRequestCacheMemberWithJuniorId(None, parsedRequest, juniorId).get
			val pb: PersistenceBroker = rc.pb
			val cb: CacheBroker = rc.cb

			implicit val format = AllSignupsResponse.format
			val signups: AllSignupsResponse = AllSignupsResponse(
				juniorId,
				JPPortal.getSignupsForReport(pb, juniorId),
				JPPortal.getWaitListTopsForReport(pb, juniorId),
				JPPortal.getWaitListsForReport(pb, juniorId)
			)


			val resultJson: JsValue = Json.toJson(signups)
			Ok(resultJson)
		} catch {
			case _: UnauthorizedAccessException => Ok("Access Denied")
			case e: Throwable => {
				println(e)
				Ok("Internal Error")
			}
		}
	})

	case class AllSignupsResponse(juniorId: Int, enrollments: List[SignupForReport], waitListTops: List[WaitListTopForReport], waitLists: List[WaitListForReport])

	object AllSignupsResponse {

		implicit val format = Json.format[AllSignupsResponse]

		def apply(v: JsValue): AllSignupsResponse = v.as[AllSignupsResponse]
	}
}
