package org.sailcbi.APIServer.Api.Endpoints.Public

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services.Authentication.PublicUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CheckEFusePublicWeb @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val logger = PA.logger
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(PublicUserType, None, parsedRequest, rc => {
			Future(Ok(rc.cb.get(PermissionsAuthority.EFUSE_REDIS_KEY_CBIDB_PUBLIC_WEB).getOrElse("-1")))
		})
	}
}