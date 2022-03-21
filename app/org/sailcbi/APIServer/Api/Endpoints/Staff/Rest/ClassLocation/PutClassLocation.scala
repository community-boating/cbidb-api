package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.ClassLocation

import com.coleji.neptune.API.{RestControllerWithDTO, ValidationError, ValidationOk, ValidationResult}
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority, UnlockedRequestCache}
import org.sailcbi.APIServer.Entities.EntityDefinitions.ClassLocation
import org.sailcbi.APIServer.Entities.access.CbiAccess
import org.sailcbi.APIServer.Entities.dto.PutClassLocationDTO
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsNumber, JsObject}
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PutClassLocation @Inject()(implicit exec: ExecutionContext) extends RestControllerWithDTO[ClassLocation, PutClassLocationDTO](ClassLocation) with InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, PutClassLocationDTO.apply)(parsed => {
			PA.withRequestCache(StaffRequestCache, CbiAccess.permissions.PERM_GENERAL_ADMIN)(None, parsedRequest, rc => {
				put(rc, parsed) match {
					case Left(ve: ValidationError) => Future(Ok(ve.toResultError.asJsObject()))
					case Right(i: ClassLocation) => Future(Ok(new JsObject(Map(
						"LOCATION_ID" -> JsNumber(i.values.locationId.get)
					))))
				}
			})
		})
	}

	override def runValidationsForUpdate(rc: UnlockedRequestCache, d: PutClassLocationDTO): ValidationResult = ValidationOk

	override def runValidationsForInsert(rc: UnlockedRequestCache, d: PutClassLocationDTO): ValidationResult = ValidationOk
}
