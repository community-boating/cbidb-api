package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.neptune.API.ValidationResult
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Logic.ApClassLogic
import org.sailcbi.APIServer.UserTypes.MemberRequestCache
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApGuidedSailController @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def getTimeSlotsForDay(forDate: LocalDate)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
			val personId = rc.getAuthedPersonId
			ApClassLogic.getApGuidedSailTimeslots(rc, forDate)
		})
	}
}