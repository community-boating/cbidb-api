package org.sailcbi.APIServer.Api.Endpoints.Security

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services.Authentication.{ApexUserType, BouncerUserType, MemberUserType, StaffUserType}
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ValidateApexSessionKey @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req =>
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCache(BouncerUserType)(None, parsedRequest, rc => {
			println(parsedRequest.postParams)
			val userName = parsedRequest.postParams("userName")
			val apexSession = parsedRequest.postParams("apexSession")
			val apexSessionKey = parsedRequest.postParams("apexSessionKey")


			Future(Ok(ApexUserType.validateApexSessionKey(rc, userName, apexSession, apexSessionKey).toString))
			//		Future(Ok("false"))
		})
	}
}