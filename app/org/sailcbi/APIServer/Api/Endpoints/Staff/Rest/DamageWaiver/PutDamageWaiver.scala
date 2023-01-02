package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.DamageWaiver

import com.coleji.neptune.API.{RestControllerWithDTO, ValidationError, ValidationOk, ValidationResult}
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority, UnlockedRequestCache}
import org.sailcbi.APIServer.Entities.EntityDefinitions.DamageWaiver
import org.sailcbi.APIServer.Entities.dto.PutDamageWaiverDTO
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsNumber, JsObject}
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PutDamageWaiver @Inject()(implicit exec: ExecutionContext) extends RestControllerWithDTO[DamageWaiver, PutDamageWaiverDTO](DamageWaiver) with InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, PutDamageWaiverDTO.apply)(parsed => {
			PA.withRequestCache(StaffRequestCache)(None, parsedRequest, rc => {
				put(rc, parsed) match {
					case Left(ve: ValidationError) => Future(Ok(ve.toResultError.asJsObject))
					case Right(i: DamageWaiver) => Future(Ok(new JsObject(Map(
						"WAIVER_ID" -> JsNumber(i.values.waiverId.get)
					))))
				}
			})
		})
	}

	override def runValidationsForUpdate(rc: UnlockedRequestCache, d: PutDamageWaiverDTO): ValidationResult = ValidationOk

	override def runValidationsForInsert(rc: UnlockedRequestCache, d: PutDamageWaiverDTO): ValidationResult = ValidationOk
}