package org.sailcbi.APIServer.Api.Endpoints.Member

import java.time.temporal.ChronoUnit

import javax.inject.Inject
import org.sailcbi.APIServer.Api.{ValidationError, ValidationOk}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Junior.JPPortal
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.{MemberUserType, ProtoPersonUserType}
import org.sailcbi.APIServer.Services.PermissionsAuthority.UnauthorizedAccessException
import org.sailcbi.APIServer.Services.{PermissionsAuthority, RequestCache, ResultSetWrapper}
import play.api.libs.json.{JsNull, JsNumber, JsObject, JsString, JsValue, Json}
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

class SignupNote @Inject()(implicit exec: ExecutionContext) extends Controller {
	def get(juniorId: Int, instanceId: Int)(implicit PA: PermissionsAuthority) = Action { request =>
		try {
			val logger = PA.logger
			val parsedRequest = ParsedRequest(request)
			val rc: RequestCache = PA.getRequestCacheMemberWithJuniorId(None, parsedRequest, juniorId)._2.get
			val pb = rc.pb

			JPPortal.getSignupNote(pb, juniorId, instanceId) match {
				case Right(os) => os match {
					case Some(s) => Ok(new JsObject(Map(
						"juniorId" -> JsNumber(juniorId),
						"instanceId" -> JsNumber(instanceId),
						"signupNote" -> JsString(s)

					)))
					case None => Ok(new JsObject(Map(
						"juniorId" -> JsNumber(juniorId),
						"instanceId" -> JsNumber(instanceId),
						"signupNote" -> JsNull

					)))
				}
				case Left(err) => Ok(err.toResultError.asJsObject())
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
	def post()(implicit PA: PermissionsAuthority) = Action { request =>
		try {
			val logger = PA.logger
			val parsedRequest = ParsedRequest(request)
			val data = request.body.asJson
			data match {
				case None => {
					println("no body")
					new Status(400)("no body")
				}
				case Some(v: JsValue) => {
					println(v)
					val parsed = SignupNoteShape.apply(v)
					val rc: RequestCache = PA.getRequestCacheMemberWithJuniorId(None, parsedRequest, parsed.juniorId)._2.get
					val pb = rc.pb
					implicit val format = SignupNoteShape.format

					JPPortal.saveSignupNote(pb, parsed.juniorId, parsed.instanceId, parsed.signupNote) match {
						case ValidationOk => Ok(Json.toJson(parsed))
						case e: ValidationError => Ok(e.toResultError.asJsObject())
					}
				}
				case Some(v) => {
					println("wut dis " + v)
					Ok("wat")
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

	case class SignupNoteShape(juniorId: Int, instanceId: Int, signupNote: Option[String])

	object SignupNoteShape{
		implicit val format = Json.format[SignupNoteShape]

		def apply(v: JsValue): SignupNoteShape = v.as[SignupNoteShape]
	}
}
