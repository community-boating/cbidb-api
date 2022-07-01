package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.ClassInstructor

import com.coleji.neptune.API.{RestControllerWithDTO, ValidationError, ValidationOk, ValidationResult}
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority, UnlockedRequestCache}
import org.sailcbi.APIServer.Entities.EntityDefinitions.ClassInstructor
import org.sailcbi.APIServer.Entities.access.CbiPermissions
import org.sailcbi.APIServer.Entities.dto.PutClassInstructorDTO
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsNumber, JsObject}
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PutClassInstructor @Inject()(implicit exec: ExecutionContext) extends RestControllerWithDTO[ClassInstructor, PutClassInstructorDTO](ClassInstructor) with InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, PutClassInstructorDTO.apply)(parsed => {
			PA.withRequestCache(StaffRequestCache, CbiPermissions.PERM_GENERAL_ADMIN)(None, parsedRequest, rc => {
				put(rc, parsed) match {
					case Left(ve: ValidationError) => Future(Ok(ve.toResultError.asJsObject()))
					case Right(i: ClassInstructor) => Future(Ok(new JsObject(Map(
						"INSTRUCTOR_ID" -> JsNumber(i.values.instructorId.get)
					))))
				}
			})
		})
	}

	override def runValidationsForUpdate(rc: UnlockedRequestCache, d: PutClassInstructorDTO): ValidationResult = ValidationOk

	override def runValidationsForInsert(rc: UnlockedRequestCache, d: PutClassInstructorDTO): ValidationResult = ValidationOk
}
