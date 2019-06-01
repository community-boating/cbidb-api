package Api

import CbiUtil.ParsedRequest
import Services.Authentication.{NonMemberUserType, UserType}
import Services.PermissionsAuthority.UnauthorizedAccessException
import Services.{PermissionsAuthority, RequestCache}
import play.api.mvc._

trait AuthenticatedRequest {

	class RequestClass extends Controller

	protected val requestClass = new RequestClass
	protected val Ok = requestClass.Ok

	protected def Status(code: Int) = requestClass.Status(code)

	protected def getRCOption(ut: NonMemberUserType, parsedRequest: ParsedRequest): Option[RequestCache] =
		PermissionsAuthority.getRequestCache(ut, None, parsedRequest)._2

	protected def getRCOptionMember(parsedRequest: ParsedRequest, juniorId: Option[Int]): Option[RequestCache] =
		PermissionsAuthority.getRequestCacheMember(None, parsedRequest, juniorId)._2

	@deprecated
	protected def getRC(ut: NonMemberUserType, parsedRequest: ParsedRequest): RequestCache = {
		getRCOption(ut, parsedRequest) match {
			case Some(rc) => rc
			case None => throw new UnauthorizedAccessException("Unable to generate RC of type " + ut)
		}
	}
}
