package org.sailcbi.APIServer.Api.Endpoints.Staff

import com.coleji.framework.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Entities.EntityDefinitions.User
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserPermissions @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val u = User.getAuthedUser(rc)
			implicit val format = PermissionsShape.format
			Future(Ok(Json.toJson(PermissionsShape(u.values.userType.get.get))))
		})
	})


	case class PermissionsShape (
		userType: String
	)

	object PermissionsShape {
		implicit val format = Json.format[PermissionsShape]
		def apply(v: JsValue): PermissionsShape = v.as[PermissionsShape]
	}
}