package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.DockReport

import com.coleji.neptune.API.{RestControllerWithDTO, ValidationError, ValidationOk, ValidationResult}
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority, UnlockedRequestCache}
import org.sailcbi.APIServer.Entities.EntityDefinitions.{DockReport, DonationFund}
import org.sailcbi.APIServer.Entities.access.CbiAccess
import org.sailcbi.APIServer.Entities.dto.{PutDockReportDto, PutDonationFundDTO}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsNull, JsNumber, JsObject}
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PutDockReport @Inject()(implicit exec: ExecutionContext) extends RestControllerWithDTO[DockReport, PutDockReportDto](DockReport) with InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, PutDockReportDto.apply)(parsed => {
			PA.withRequestCache(StaffRequestCache, CbiAccess.permissions.PERM_GENERAL_ADMIN)(None, parsedRequest, rc => {
				put(rc, parsed) match {
					case Left(ve: ValidationError) => Future(Ok(ve.toResultError.asJsObject()))
					case Right(i: DonationFund) => Future(Ok(JsNull))
				}
			})
		})
	}

	override def runValidationsForUpdate(rc: UnlockedRequestCache, d: PutDockReportDto): ValidationResult = ValidationOk

	override def runValidationsForInsert(rc: UnlockedRequestCache, d: PutDockReportDto): ValidationResult = ValidationOk
}