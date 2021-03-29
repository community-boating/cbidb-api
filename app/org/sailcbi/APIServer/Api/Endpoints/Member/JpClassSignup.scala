package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.framework.API.{ValidationError, ValidationOk}
import com.coleji.framework.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.MemberRequestCache
import play.api.libs.json.{JsNumber, JsObject, JsValue, Json}
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class JpClassSignup @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, JpClassSignupPostShape.apply)(parsed => {
			MemberRequestCache.withRequestCacheMemberWithJuniorId(parsedRequest, parsed.juniorId, rc => {
				println(parsed)

				val doEnroll = parsed.doEnroll
				val wlJoin = doEnroll && PortalLogic.canWaitListJoin(rc, parsed.juniorId, parsed.instanceId)

				//any:            see_type
				//any:            see_instance
				//any:            already_started
				//waitlist:       wl_exists
				//enroll:         spots_left
				//wljoin:         wl record exists
				//enroll/wljoin:  allow_enroll
				lazy val seeType = PortalLogic.seeTypeFromInstanceIdAsValidationResult(rc, parsed.juniorId, parsed.instanceId)
				lazy val seeInstance = PortalLogic.seeInstanceAsValidationResult(rc, parsed.juniorId, parsed.instanceId)
				lazy val alreadyStarted = PortalLogic.alreadyStartedAsValidationResult(rc, parsed.instanceId)
				lazy val wlExistsOnClass = {
					if (doEnroll) ValidationOk
					else PortalLogic.waitListExists(rc, parsed.instanceId)
				}
				lazy val hasSeats = {
					if (doEnroll && !wlJoin) PortalLogic.hasSpotsLeft(rc, parsed.instanceId, Some("The class is full."))
					else ValidationOk
				}
				lazy val allowEnroll = {
					if (doEnroll) PortalLogic.allowEnrollAsValidationResult(rc, parsed.juniorId, parsed.instanceId)
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
						val signupId = PortalLogic.actuallyEnroll(rc, parsed.instanceId, parsed.juniorId, None, doEnroll=doEnroll, fullEnroll = true, None).orNull
						Future(Ok(new JsObject(Map("signupId" -> JsNumber(signupId.toInt)))))
					}
					case e: ValidationError => Future(Ok(e.toResultError.asJsObject()))
				}
			})
		})
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


