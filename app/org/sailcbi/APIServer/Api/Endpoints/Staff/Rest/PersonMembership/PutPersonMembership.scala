package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.PersonMembership

import com.coleji.neptune.API.{RestControllerWithDTO, ValidationError, ValidationOk, ValidationResult}
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority, UnlockedRequestCache}
import org.sailcbi.APIServer.Entities.EntityDefinitions.PersonMembership
import org.sailcbi.APIServer.Entities.dto.PutPersonMembershipDTO
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsNumber, JsObject}
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PutPersonMembership @Inject()(implicit exec: ExecutionContext) extends RestControllerWithDTO[PersonMembership, PutPersonMembershipDTO](PersonMembership) with InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, PutPersonMembershipDTO.apply)(parsed => {
			PA.withRequestCache(StaffRequestCache)(None, parsedRequest, rc => {
				put(rc, parsed) match {
					case Left(ve: ValidationError) => Future(Ok(ve.toResultError.asJsObject))
					case Right(i: PersonMembership) => Future(Ok(new JsObject(Map(
						"ASSIGN_ID" -> JsNumber(i.values.assignId.get)
					))))
				}
			})
		})
	}

	override def runValidationsForUpdate(rc: UnlockedRequestCache, d: PutPersonMembershipDTO): ValidationResult = ValidationOk

	override def runValidationsForInsert(rc: UnlockedRequestCache, d: PutPersonMembershipDTO): ValidationResult = ValidationOk
}