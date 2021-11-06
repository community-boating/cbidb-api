package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.DonationFund

import com.coleji.neptune.API.RestController
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Entities.EntityDefinitions.DonationFund
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetDonationFund @Inject()(implicit val exec: ExecutionContext) extends RestController(DonationFund) with InjectedController {
	def getAll()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val funds = getByFilters(rc, List.empty, Set.empty)
			Future(Ok(Json.toJson(funds)))
		})
	})
}

