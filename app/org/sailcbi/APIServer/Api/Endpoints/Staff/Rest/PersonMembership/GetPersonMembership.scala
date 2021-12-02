package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.PersonMembership

import com.coleji.neptune.API.RestController
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Entities.EntityDefinitions.PersonMembership
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetPersonMembership @Inject()(implicit val exec: ExecutionContext) extends RestController(PersonMembership) with InjectedController {
	def getAllForPerson(personId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val pms = getByFilters(rc, List(PersonMembership.fields.personId.equalsConstant(personId)), Set(
				PersonMembership.fields.assignId,
				PersonMembership.fields.personId,
			))
			Future(Ok(Json.toJson(pms)))
		})
	})
}