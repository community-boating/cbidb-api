package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.Api.ValidationError
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Junior.JPPortal
import org.sailcbi.APIServer.Services.Authentication.ProtoPersonUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority.UnauthorizedAccessException
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker, RequestCache}
import play.api.libs.json.{JsNumber, JsObject, JsValue}
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

class DeleteJuniorClassReservation @Inject()(implicit exec: ExecutionContext) extends Controller {
	def post()(implicit PA: PermissionsAuthority) = Action { request =>
		try {
			val logger = PA.logger
			val parsedRequest = ParsedRequest(request)
			val rc: RequestCache = PA.getRequestCache(ProtoPersonUserType, None, parsedRequest)._2.get
			val pb = rc.pb
			parsedRequest.postParams.get("name") match {
				case None => {
					println("no body")
					new Status(400)("no body")
				}
				case Some(name: String) => {
					println(name)

					JPPortal.deleteProtoJunior(pb, ProtoPersonUserType.getAuthedPersonId(rc.auth.userName, pb).get, name)
					Ok("WIP")
				}
			}
		} catch {
			case _: UnauthorizedAccessException => Ok("Access Denied")
			case e: Throwable => {
				println(e)
				e.printStackTrace()
				Ok("Internal Error")
			}
		}
	}
}