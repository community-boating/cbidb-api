package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.Api.{ResultError, ValidationOk, ValidationResult}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Junior.JPPortal
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker, RequestCache, ResultSetWrapper}
import org.sailcbi.APIServer.Services.PermissionsAuthority.UnauthorizedAccessException
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

class JpClassSignup @Inject()(implicit exec: ExecutionContext) extends Controller {
	def post()(implicit PA: PermissionsAuthority) = Action { request =>
		try {
			val parsedRequest = ParsedRequest(request)
			parsedRequest.postJSON match {
				case None => {
					println("no body")
					Ok(ResultError.UNKNOWN)
				}
				case Some(v: JsValue) => {
					val parsed = JpClassSignupPostShape.apply(v)
					val rc: RequestCache = PA.getRequestCacheMemberWithJuniorId(None, parsedRequest, parsed.juniorId)._2.get
					val pb: PersistenceBroker = rc.pb
					println(parsed)


					lazy val seeType = JPPortal.seeTypeFromInstanceIdAsValidationResult(pb, parsed.juniorId, parsed.instanceId)
					lazy val seeInstance = JPPortal.seeInstanceAsValidationResult(pb, parsed.juniorId, parsed.instanceId)
					lazy val alreadyStarted = JPPortal.alreadyStartedAsValidationResult(pb, parsed.instanceId)
					lazy val wlRecordExists = if (parsed.doEnroll) ValidationOk else JPPortal.waitListExists(pb, parsed.instanceId)

					Ok("cool")
				}
				case Some(v) => {
					println("wut dis " + v)
					Ok(ResultError.UNKNOWN)
				}
			}

		} catch {
			case _: UnauthorizedAccessException => Ok("Access Denied")
			case e: Throwable => {
				println(e)
				Ok("Internal Error")
			}
		}
	}

	case class JpClassSignupPostShape (
		juniorId: Int,
		instanceId: Int,
		doEnroll: Boolean
	)

	object JpClassSignupPostShape {
		implicit val format = Json.format[JpClassSignupPostShape]

		def apply(v: JsValue): JpClassSignupPostShape = v.as[JpClassSignupPostShape]
	}
}


//any:            see_type
//any:            see_instance
//any:            already_started
//waitlist:       wl_exists
//enroll:         spots_left
//wljoin:         wl record exists
//enroll/wljoin:  allow_enroll