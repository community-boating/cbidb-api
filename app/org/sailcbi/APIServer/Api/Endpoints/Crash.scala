package org.sailcbi.APIServer.Api.Endpoints

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.IO.JP.GetWeeks
import org.sailcbi.APIServer.IO.JP.GetWeeks.GetWeeksResult
import org.sailcbi.APIServer.UserTypes.PublicRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class Crash @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def crash()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req => {
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCache(PublicRequestCache)(None, parsedRequest, rc => {
			throw new Exception("Someone crashed me with /auth/crash!")
			Future(Ok("crash"))
		})
	}}
}

