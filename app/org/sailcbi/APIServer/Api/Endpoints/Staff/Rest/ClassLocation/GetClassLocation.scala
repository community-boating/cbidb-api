package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.ClassLocation

import com.coleji.neptune.API.RestController
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Entities.EntityDefinitions.ClassLocation
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetClassLocation @Inject()(implicit val exec: ExecutionContext) extends RestController(ClassLocation) with InjectedController {
	def getAll()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val locations = getByFilters(rc, List.empty, Set(
				ClassLocation.fields.locationId,
				ClassLocation.fields.locationName,
				ClassLocation.fields.active,
			))
			Future(Ok(Json.toJson(locations)))
		})
	})
}