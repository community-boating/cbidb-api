package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.HighSchool

import com.coleji.neptune.API.RestController
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Entities.EntityDefinitions.HighSchool
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetSchool @Inject()(implicit val exec: ExecutionContext) extends RestController(HighSchool) with InjectedController {
	def getAll()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val schools = getByFilters(rc, List.empty, Set(
				HighSchool.fields.schoolId,
				HighSchool.fields.schoolName,
				HighSchool.fields.active,
			))
			Future(Ok(Json.toJson(schools)))
		})
	})
}
