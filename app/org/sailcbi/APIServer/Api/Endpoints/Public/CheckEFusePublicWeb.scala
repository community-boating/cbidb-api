package org.sailcbi.APIServer.Api.Endpoints.Public

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services.Authentication.PublicUserType
import org.sailcbi.APIServer.Services.Exception.UnauthorizedAccessException
import org.sailcbi.APIServer.Services.{PermissionsAuthority, RequestCache}
import play.api.mvc.InjectedController

import scala.concurrent.ExecutionContext

class CheckEFusePublicWeb @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority) = Action { request =>
		try {
			val logger = PA.logger
			val parsedRequest = ParsedRequest(request)
			val rc: RequestCache = PA.getRequestCache(PublicUserType, None, parsedRequest).get
			Ok(rc.cb.get(PermissionsAuthority.EFUSE_REDIS_KEY_CBIDB_PUBLIC_WEB).getOrElse("-1"))

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