package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.GuestPriv

import com.coleji.neptune.API.{RestControllerWithDTO, ValidationError, ValidationOk, ValidationResult}
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority, UnlockedRequestCache}
import org.sailcbi.APIServer.Entities.EntityDefinitions.GuestPriv
import org.sailcbi.APIServer.Entities.dto.PutGuestPrivDTO
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsNumber, JsObject}
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PutGuestPriv @Inject()(implicit exec: ExecutionContext) extends RestControllerWithDTO[GuestPriv, PutGuestPrivDTO](GuestPriv) with InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, PutGuestPrivDTO.apply)(parsed => {
			PA.withRequestCache(StaffRequestCache)(None, parsedRequest, rc => {
				put(rc, parsed) match {
					case Left(ve: ValidationError) => Future(Ok(ve.toResultError.asJsObject))
					case Right(i: GuestPriv) => Future(Ok(new JsObject(Map(
						"MEMBERSHIP_ID" -> JsNumber(i.values.membershipId.get)
					))))
				}
			})
		})
	}

	override def runValidationsForUpdate(rc: UnlockedRequestCache, d: PutGuestPrivDTO): ValidationResult = ValidationOk

	override def runValidationsForInsert(rc: UnlockedRequestCache, d: PutGuestPrivDTO): ValidationResult = ValidationOk
}
