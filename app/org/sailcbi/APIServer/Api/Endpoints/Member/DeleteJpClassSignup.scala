package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.Api.{ResultError, ValidationError, ValidationOk}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Junior.JPPortal
import org.sailcbi.APIServer.Services.Exception.UnauthorizedAccessException
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker, RequestCache}
import play.api.libs.json.{JsBoolean, JsNumber, JsObject, JsValue, Json}
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

class DeleteJpClassSignup @Inject()(implicit exec: ExecutionContext) extends Controller {
	def post()(implicit PA: PermissionsAuthority) = Action { request =>
		try {
			val parsedRequest = ParsedRequest(request)
			parsedRequest.postJSON match {
				case None => {
					println("no body")
					Ok(ResultError.UNKNOWN)
				}
				case Some(v: JsValue) => {
					val parsed = JpClassSignupDeletePostShape.apply(v)
					val rc: RequestCache = PA.getRequestCacheMemberWithJuniorId(None, parsedRequest, parsed.juniorId).get
					val pb: PersistenceBroker = rc.pb
					println(parsed)

					JPPortal.attemptDeleteSignup(pb, parsed.juniorId, parsed.instanceId) match {
						case ValidationOk => Ok(new JsObject(Map("success" -> JsBoolean(true))))
						case e: ValidationError => Ok(e.toResultError.asJsObject())
					}
				}
				case Some(v) => {
					println("wut dis " + v)
					Ok(ResultError.UNKNOWN)
				}
			}

		} catch {
			case e: UnauthorizedAccessException => {
				e.printStackTrace()
				Ok("Access Denied")
			}
			case e: Throwable => {
				println(e)
				Ok("Internal Error")
			}
		}
	}

	case class JpClassSignupDeletePostShape (
		juniorId: Int,
		instanceId: Int
	)

	object JpClassSignupDeletePostShape {
		implicit val format = Json.format[JpClassSignupDeletePostShape]

		def apply(v: JsValue): JpClassSignupDeletePostShape = v.as[JpClassSignupDeletePostShape]
	}
}
