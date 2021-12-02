package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.GuestPriv

import com.coleji.neptune.API.RestController
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.Storable.StorableQuery.{QueryBuilder, TableAlias}
import org.sailcbi.APIServer.Entities.EntityDefinitions.{GuestPriv, PersonMembership}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetGuestPriv @Inject()(implicit val exec: ExecutionContext) extends RestController(GuestPriv) with InjectedController {
	def getAllForPerson(personId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val tableGp = TableAlias.wrapForInnerJoin(GuestPriv)
			val tablePm = TableAlias.wrapForInnerJoin(PersonMembership)
			val q = QueryBuilder
				.from(tableGp)
				.innerJoin(tablePm, tablePm.wrappedFields(_.fields.assignId).wrapFilter(_.equalsField(tableGp.wrappedFields(_.fields.membershipId))))
				.where(tablePm.wrappedFields(_.fields.personId).wrapFilter(_.equalsConstant(personId)))
				.select(List(
					GuestPriv.fields.membershipId.alias(tableGp),
					GuestPriv.fields.price.alias(tableGp)
				))

			val gps = rc.executeQueryBuilder(q).map(tableGp.construct)

			Future(Ok(Json.toJson(gps)))
		})
	})
}
