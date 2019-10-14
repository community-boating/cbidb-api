package org.sailcbi.APIServer.Api.Endpoints.Security

import javax.inject.Inject
import org.sailcbi.APIServer.Api.Endpoints.Member.AddJuniorClassReservationShape
import org.sailcbi.APIServer.Api.ValidationError
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Junior.JPPortal
import org.sailcbi.APIServer.Services.Authentication.ProtoPersonUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority.UnauthorizedAccessException
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker, RequestCache}
import play.api.libs.json.{JsNumber, JsObject, JsValue, Json}
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

class CreateMember @Inject()(implicit exec: ExecutionContext) extends Controller {
	def post()(implicit PA: PermissionsAuthority) = Action { request =>
		try {
			val parsedRequest = ParsedRequest(request)
			println(parsedRequest)
			parsedRequest.postJSON match {
				case Some(v: JsValue) => {
					val cms = CreateMemberShape.apply(v)
					val protoPersonCookieValMaybe = parsedRequest.cookies.find(_.name == ProtoPersonUserType.COOKIE_NAME).map(_.value)
					println(cms)
					println(protoPersonCookieValMaybe)
					createMember(
						firstName = cms.firstName,
						lastName = cms.lastName,
						username = cms.username,
						pwHash = cms.pwHash,
						protoPersonValue = protoPersonCookieValMaybe
					) match {
						case Left(v: ValidationError) => Ok(v.toResultError().asJsObject())
						case Right(id: Int) => Ok(new JsObject(Map(
							"personId" -> JsNumber(id)
						)))
					}
				}
				case None => Ok("Internal Error")
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

	case class CreateMemberShape(
		username: String,
		firstName: String,
		lastName: String,
		pwHash: String
	)

	object CreateMemberShape {
		implicit val format = Json.format[CreateMemberShape]

		def apply(v: JsValue): CreateMemberShape = v.as[CreateMemberShape]
	}

	def createMember(
		firstName: String,
		lastName: String,
		username: String,
		pwHash: String,
		protoPersonValue: Option[String]
	): Either[ValidationError, Int] = {
		val nothingBlank = List(
			ValidationError.inline(firstName)(s => s != null && s.length > 0, "First Name must be specified."),
			ValidationError.inline(lastName)(s => s != null && s.length > 0, "Last Name must be specified."),
			ValidationError.inline(username)(s => s != null && s.length > 0, "Email must be specified.")
		)

		ValidationError.combine(nothingBlank.filter(_.isDefined).map(_.orNull)) match {
			case Some(v) => Left(v)
			case None => Right(-1)
		}
	}
}
