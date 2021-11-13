package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.Person

import com.coleji.neptune.API.RestController
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Entities.EntityDefinitions.Person
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetPerson @Inject()(implicit val exec: ExecutionContext) extends RestController(Person) with InjectedController {
	def getOneForSummaryScreen(personId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val person = getOne(rc, personId, Set(
				Person.fields.personId,
				Person.fields.nameFirst,
				Person.fields.nameLast
			))
			Future(Ok(Json.toJson(person)))
		})
	})
}