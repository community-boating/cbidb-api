package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.Tag

import com.coleji.framework.API.RestController
import com.coleji.framework.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Entities.EntityDefinitions.Tag
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetTag @Inject()(implicit val exec: ExecutionContext) extends RestController(Tag) with InjectedController {
	def getAll()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val tags = getByFilters(rc, List.empty, Set.empty)
			Future(Ok(Json.toJson(tags)))
		})
	})
}
