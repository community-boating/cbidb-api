package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.ClassInstructor

import com.coleji.neptune.API.RestController
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Api.Endpoints.Dto.Staff.Rest.ClassInstructor.DtoStaffRestClassInstructorGetResponseSuccess
import org.sailcbi.APIServer.Entities.EntityDefinitions.ClassInstructor
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetClassInstructor @Inject()(implicit val exec: ExecutionContext) extends RestController(ClassInstructor) with InjectedController {
	def getAll()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val instructors = getByFilters(rc, List.empty, Set(
				ClassInstructor.fields.instructorId,
				ClassInstructor.fields.nameFirst,
				ClassInstructor.fields.nameLast,
			))
			val ret = instructors.map(i => new DtoStaffRestClassInstructorGetResponseSuccess(
				instructorId = i.values.instructorId.get,
				nameFirst = i.values.nameFirst.get,
				nameLast = i.values.nameLast.get
			))
			Future(Ok(Json.toJson(ret)))
		})
	})
}
