package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.DamageWaiver

import com.coleji.neptune.API.RestController
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Entities.EntityDefinitions.DamageWaiver
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetDamageWaiver @Inject()(implicit val exec: ExecutionContext) extends RestController(DamageWaiver) with InjectedController {
	def getAllForPerson(personId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val dws = getByFilters(rc, List(DamageWaiver.fields.personId.alias.equalsConstant(personId)), Set(
				DamageWaiver.fields.waiverId,
				DamageWaiver.fields.personId,
				DamageWaiver.fields.price
			))
			Future(Ok(Json.toJson(dws)))
		})
	})
}