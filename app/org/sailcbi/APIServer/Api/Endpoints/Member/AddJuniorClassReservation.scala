package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.Api.{ValidationError, ValidationResult}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Junior.JPPortal
import org.sailcbi.APIServer.Services.Authentication.ProtoPersonUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority.UnauthorizedAccessException
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker, RequestCache}
import play.api.libs.json.{JsNumber, JsObject, JsValue}
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

class AddJuniorClassReservation @Inject()(implicit exec: ExecutionContext) extends Controller {
	def post()(implicit PA: PermissionsAuthority) = Action { request =>
		try {
			val logger = PA.logger
			val parsedRequest = ParsedRequest(request)
			val rc: RequestCache = PA.getRequestCache(ProtoPersonUserType, None, parsedRequest)._2.get
			val data = request.body.asJson
			data match {
				case None => {
					println("no body")
					new Status(400)("no body")
				}
				case Some(v: JsValue) => {
					println(v)
					val parsed = AddJuniorClassReservationShape.apply(v)

					doPost(rc, parsed) match {
						case Left(err) => Ok(err.toResultError.asJsObject())
						case Right(juniorId) => Ok(new JsObject(Map(
							"personId" -> JsNumber(juniorId)
						)))
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

	private def doPost(rc: RequestCache, body: AddJuniorClassReservationShape): Either[ValidationError, Int] = {
		if (body.juniorFirstName == null || body.juniorFirstName.length() == 0) {
			Left(ValidationResult.from("Please specify junior name."))
		} else {
			val pb: PersistenceBroker = rc.pb

			// Create protoparent if it doenst exist
			val protoParentPersonId = ProtoPersonUserType.getAuthedPersonId(rc.auth.userName, pb)
			val parentPersonId = {
				if (protoParentPersonId.isDefined) {
					val ret = protoParentPersonId.get
					println("reusing existing protoparent record for this cookie val " + ret)
					ret
				} else {
					val ret = JPPortal.persistProtoParent(pb, rc.auth.userName)
					println("created new protoparent: " + ret)
					ret
				}
			}

			// Create new protojunior
			val (juniorPersonId, rollbackCreateJunior) = JPPortal.persistProtoJunior(pb, parentPersonId, body.juniorFirstName)

			// create new signup with the min(signup_time) of all this protoparent's other signups
			val minSignupTime = JPPortal.getMinSignupTimeForParent(pb, parentPersonId)
			println("min signup is " + minSignupTime)
			val signupResult = JPPortal.attemptSignup(pb, juniorPersonId, body.beginnerInstanceId, body.intermediateInstanceId, minSignupTime)
			println(" result is:" + signupResult)
			if (signupResult.isDefined) {
				rollbackCreateJunior()
				Left(ValidationResult.from(signupResult.get))
			} else Right(juniorPersonId)
		}
	}
}
