package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.GuestPriv

import com.coleji.neptune.API.RestController
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.Storable.StorableQuery.QueryBuilder
import org.sailcbi.APIServer.Entities.EntityDefinitions.{GuestPriv, PersonMembership}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetGuestPriv @Inject()(implicit val exec: ExecutionContext) extends RestController(GuestPriv) with InjectedController {
	def getAllForPerson(personId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val gp = GuestPriv.alias
			val pm = PersonMembership.alias

			val q = QueryBuilder
				.from(gp)
				.innerJoin(pm,
					PersonMembership.fields.assignId.alias(pm) equalsField GuestPriv.fields.membershipId.alias(gp)
				)
				.where(PersonMembership.fields.personId.alias(pm) equalsConstant personId)
				.select(List(
					gp.wrappedFields(_.membershipId),
					gp.wrappedFields(_.price)
				))

			val gps = rc.executeQueryBuilder(q).map(gp.construct)

			Future(Ok(Json.toJson(gps)))
		})
	})
}
