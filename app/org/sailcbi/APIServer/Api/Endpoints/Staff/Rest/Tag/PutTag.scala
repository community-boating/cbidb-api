package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.Tag

import com.coleji.neptune.API.{RestControllerWithDTO, ValidationError, ValidationOk, ValidationResult}
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority, UnlockedRequestCache}
import org.sailcbi.APIServer.Entities.EntityDefinitions.Tag
import org.sailcbi.APIServer.Entities.access.CbiPermissions
import org.sailcbi.APIServer.Entities.dto.PutTagDTO
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsNumber, JsObject}
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PutTag @Inject()(implicit exec: ExecutionContext) extends RestControllerWithDTO[Tag, PutTagDTO](Tag) with InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, PutTagDTO.apply)(parsed => {
			PA.withRequestCache(StaffRequestCache, CbiPermissions.PERM_UPDATE_TAGS)(None, parsedRequest, rc => {
				put(rc, parsed) match {
					case Left(ve: ValidationError) => Future(Ok(ve.toResultError.asJsObject))
					case Right(i: Tag) => Future(Ok(new JsObject(Map(
						"TAG_ID" -> JsNumber(i.values.tagId.get)
					))))
				}
			})
		})
	}

	override def runValidationsForUpdate(rc: UnlockedRequestCache, d: PutTagDTO): ValidationResult = ValidationOk

	override def runValidationsForInsert(rc: UnlockedRequestCache, d: PutTagDTO): ValidationResult = ValidationOk
}
