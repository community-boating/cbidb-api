package org.sailcbi.APIServer.Api.Endpoints

import com.coleji.framework.Core.PermissionsAuthority
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsObject, JsString}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IsLoggedInAsStaff @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		PA.withRequestCache[StaffRequestCache](StaffRequestCache)(None, ParsedRequest(request), rc => {
			Future(Ok(JsObject(Map("value" -> JsString(rc.userName)))))
		})
	}
}
