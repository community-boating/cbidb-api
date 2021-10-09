package org.sailcbi.APIServer.Api.Endpoints.Public

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.UserTypes.PublicRequestCache
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CheckEFusePublicWeb @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val logger = PA.logger
		val parsedRequest = ParsedRequest(request)
		val EFUSE_REDIS_KEY_CBIDB_PUBLIC_WEB = "$$CBIDB_PUBLIC_WEB_EFUSE"
		PA.withRequestCache(PublicRequestCache)(None, parsedRequest, rc => {
			Future(Ok(rc.cb.get(EFUSE_REDIS_KEY_CBIDB_PUBLIC_WEB).getOrElse("-1")))
		})
	}
}