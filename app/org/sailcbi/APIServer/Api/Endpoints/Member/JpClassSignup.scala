package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.Api.{ResultError, ValidationError, ValidationOk, ValidationResult}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Junior.JPPortal
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker, RequestCache, ResultSetWrapper}
import org.sailcbi.APIServer.Services.Exception.UnauthorizedAccessException
import play.api.libs.json.{JsNumber, JsObject, JsValue, Json}
import play.api.mvc.{Action, Controller}

import scala.collection.JavaConverters._
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
					val rc: RequestCache = PA.getRequestCacheMemberWithJuniorId(None, parsedRequest, parsed.juniorId).get
					val pb: PersistenceBroker = rc.pb
					println(parsed)

					val doEnroll = parsed.doEnroll
					val wlJoin = doEnroll && JPPortal.canWaitListJoin(pb, parsed.juniorId, parsed.instanceId)

					//any:            see_type
					//any:            see_instance
					//any:            already_started
					//waitlist:       wl_exists
					//enroll:         spots_left
					//wljoin:         wl record exists
					//enroll/wljoin:  allow_enroll
					lazy val seeType = JPPortal.seeTypeFromInstanceIdAsValidationResult(pb, parsed.juniorId, parsed.instanceId)
					lazy val seeInstance = JPPortal.seeInstanceAsValidationResult(pb, parsed.juniorId, parsed.instanceId)
					lazy val alreadyStarted = JPPortal.alreadyStartedAsValidationResult(pb, parsed.instanceId)
					lazy val wlExistsOnClass = {
						if (doEnroll) ValidationOk
						else JPPortal.waitListExists(pb, parsed.instanceId)
					}
					lazy val hasSeats = {
						if (doEnroll && !wlJoin) JPPortal.hasSpotsLeft(pb, parsed.instanceId, Some("The class is full."))
						else ValidationOk
					}
					lazy val allowEnroll = {
						if (doEnroll) JPPortal.allowEnrollAsValidationResult(pb, parsed.juniorId, parsed.instanceId)
						else ValidationOk
					}

					val finalResult = for {
						_ <- seeType
						_ <- seeInstance
						_ <- alreadyStarted
						_ <- wlExistsOnClass
						_ <- hasSeats
						x <- allowEnroll
					} yield x

					finalResult match {
						case ValidationOk => {
							val signupId = JPPortal.actuallyEnroll(pb, parsed.instanceId, parsed.juniorId, None, doEnroll=doEnroll, fullEnroll = true, None).orNull
							Ok(new JsObject(Map("signupId" -> JsNumber(signupId.toInt))))
						}
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


