package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.Event

import com.coleji.neptune.API.RestController
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Entities.EntityDefinitions.Event
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetEvent @Inject()(implicit val exec: ExecutionContext) extends RestController(Event) with InjectedController {
	def getAll()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val events = getByFilters(rc, List.empty, Set(
				Event.fields.eventId,
				Event.fields.eventName,
				Event.fields.eventDate,
			))
			Future(Ok(Json.toJson(events)))
		})
	})
}
