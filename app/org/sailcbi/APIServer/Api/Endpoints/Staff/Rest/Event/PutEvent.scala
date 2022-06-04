package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.Event

import com.coleji.neptune.API.{RestControllerWithDTO, ValidationError, ValidationOk, ValidationResult}
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority, UnlockedRequestCache}
import org.sailcbi.APIServer.Entities.EntityDefinitions.Event
import org.sailcbi.APIServer.Entities.access.CbiAccess
import org.sailcbi.APIServer.Entities.dto.PutEventDTO
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsNumber, JsObject}
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PutEvent @Inject()(implicit exec: ExecutionContext) extends RestControllerWithDTO[Event, PutEventDTO](Event) with InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, PutEventDTO.apply)(parsed => {
			PA.withRequestCache(StaffRequestCache, CbiAccess.permissions.PERM_GENERAL_ADMIN)(None, parsedRequest, rc => {
				put(rc, parsed) match {
					case Left(ve: ValidationError) => Future(Ok(ve.toResultError.asJsObject()))
					case Right(i: Event) => Future(Ok(new JsObject(Map(
						"EVENT_ID" -> JsNumber(i.values.eventId.get)
					))))
				}
			})
		})
	}

	override def runValidationsForUpdate(rc: UnlockedRequestCache, d: PutEventDTO): ValidationResult = ValidationOk

	override def runValidationsForInsert(rc: UnlockedRequestCache, d: PutEventDTO): ValidationResult = ValidationOk
}
