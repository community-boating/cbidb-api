package org.sailcbi.APIServer.Api.Endpoints.Staff

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserPermissions @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			println(rc.upa)
			Future(Ok(Json.toJson(rc.upa.permissionList)))
		})
	})
}