package org.sailcbi.APIServer.Api.Endpoints

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services.Authentication.StaffUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.libs.json.{JsObject, JsString}
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.{ExecutionContext, Future}

class IsLoggedInAsStaff @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		PA.withRequestCache(StaffUserType, None, ParsedRequest(request), rc => {
			Future(Ok(JsObject(Map("value" -> JsString(rc.auth.userName)))))
		})
	}
}
