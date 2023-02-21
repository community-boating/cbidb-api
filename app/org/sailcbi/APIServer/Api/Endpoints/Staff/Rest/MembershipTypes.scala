package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest

import com.coleji.neptune.API.RestController
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Entities.EntityDefinitions.{BoatType, MembershipType}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MembershipTypes @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def getAll()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val memTypes = rc.getObjectsByFilters(MembershipType, List(MembershipType.fields.active.alias.equals(true)), Set(
				MembershipType.fields.membershipTypeId,
				MembershipType.fields.membershipTypeName,
				MembershipType.fields.active,
				MembershipType.fields.displayOrder
			)).sortWith((a, b) => a.values.displayOrder.get < b.values.displayOrder.get)
			Future(Ok(Json.toJson(memTypes)))
		})
	})
}