package org.sailcbi.APIServer.Api.Endpoints.Staff

import com.coleji.framework.Core.PermissionsAuthority
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Entities.EntityDefinitions.User
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.UserTypes.BouncerRequestCache
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UpdateUserPassword @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, UpdateUserPasswordShape.apply)(parsed => {
			PA.withRequestCache(BouncerRequestCache)(None, parsedRequest, rc => {
				rc.getUserByUsername(parsed.username) match {
					case Some(user) => {
						user.values.pwHash.update(Some(parsed.pwHash))
						user.values.pwHashScheme.update(Some(MagicIds.PW_HASH_SCHEME.STAFF_2))
						rc.updateUser(user)
					}
					case None =>
				}
				Future(Ok("success"))
			})
		})
	}

	case class UpdateUserPasswordShape(username: String, pwHash: String)
	object UpdateUserPasswordShape{
		implicit val format = Json.format[UpdateUserPasswordShape]
		def apply(v: JsValue): UpdateUserPasswordShape = v.as[UpdateUserPasswordShape]
	}
}
