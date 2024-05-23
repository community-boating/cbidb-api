package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.MemberRequestCache
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

case class ApClassForCalendarResponse(personId: Int, instances: List[PortalLogic.ApClassInstanceForCalendar])

object ApClassForCalendarResponse {
	implicit val format: OFormat[ApClassForCalendarResponse] = Json.format[ApClassForCalendarResponse]
}

class GetApClassesForCalendar @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
			val personId = rc.getAuthedPersonId

			val instances = PortalLogic.getApClassesForCalendar(rc, personId)

			val response = ApClassForCalendarResponse(personId, instances)

			Future(Ok(Json.toJson(response)))
		})
	})
}
