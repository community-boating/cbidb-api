package org.sailcbi.APIServer.Api

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services.Authentication.NonMemberUserType
import org.sailcbi.APIServer.Services.Exception.UnauthorizedAccessException
import org.sailcbi.APIServer.Services.{PermissionsAuthority, RequestCache}
import play.api.mvc._

trait AuthenticatedRequest {
	class RequestClass extends Controller

	protected val requestClass = new RequestClass
	protected val Ok = requestClass.Ok

	protected def Status(code: Int) = requestClass.Status(code)

		protected def getRCOption(ut: NonMemberUserType, parsedRequest: ParsedRequest)(implicit PA: PermissionsAuthority): Option[RequestCache] =
		PA.getRequestCache(ut, None, parsedRequest)

		protected def getRCOptionMember(parsedRequest: ParsedRequest)(implicit PA: PermissionsAuthority): Option[RequestCache] =
		PA.getRequestCacheMember(None, parsedRequest)

		@deprecated
		protected def getRC(ut: NonMemberUserType, parsedRequest: ParsedRequest): RequestCache = {
		getRCOption(ut, parsedRequest) match {
			case Some(rc) => rc
			case None => throw new UnauthorizedAccessException("Unable to generate RC of type " + ut)
		}
	}
}
