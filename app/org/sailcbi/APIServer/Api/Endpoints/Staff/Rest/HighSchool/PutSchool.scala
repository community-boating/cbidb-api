package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.HighSchool

import com.coleji.neptune.API.{RestControllerWithDTO, ValidationError, ValidationOk, ValidationResult}
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority, UnlockedRequestCache}
import org.sailcbi.APIServer.Entities.EntityDefinitions.HighSchool
import org.sailcbi.APIServer.Entities.access.CbiPermissions
import org.sailcbi.APIServer.Entities.dto.PutHighSchoolDTO
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsNumber, JsObject}
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PutSchool @Inject()(implicit exec: ExecutionContext) extends RestControllerWithDTO[HighSchool, PutHighSchoolDTO](HighSchool) with InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, PutHighSchoolDTO.apply)(parsed => {
			PA.withRequestCache(StaffRequestCache, CbiPermissions.PERM_UPDATE_SCHOOLS)(None, parsedRequest, rc => {
				put(rc, parsed) match {
					case Left(ve: ValidationError) => Future(Ok(ve.toResultError.asJsObject))
					case Right(i: HighSchool) => Future(Ok(new JsObject(Map(
						"SCHOOL_ID" -> JsNumber(i.values.schoolId.get)
					))))
				}
			})
		})
	}

	override def runValidationsForUpdate(rc: UnlockedRequestCache, d: PutHighSchoolDTO): ValidationResult = ValidationOk

	override def runValidationsForInsert(rc: UnlockedRequestCache, d: PutHighSchoolDTO): ValidationResult = ValidationOk
}