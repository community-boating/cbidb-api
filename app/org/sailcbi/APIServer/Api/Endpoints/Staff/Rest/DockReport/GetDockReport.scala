package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.DockReport

import com.coleji.neptune.API.RestController
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Entities.EntityDefinitions.DockReport
import org.sailcbi.APIServer.Entities.dto.PutDockReportDto
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsNull, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetDockReport @Inject()(implicit val exec: ExecutionContext) extends RestController(DockReport) with InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val dockReport = getByFilters(rc, List(DockReport.fields.reportDate.alias.isDateConstant(LocalDate.now)), Set.empty)
				.map(dr => new PutDockReportDto(
					DOCK_REPORT_ID=Some(dr.values.dockReportId.get),
					REPORT_DATE=dr.values.reportDate.get,
					SUNSET_DATETIME=dr.values.sunsetDatetime.get,
					INCIDENTS_NOTES=dr.values.incidentsNotes.get,
					ANNOUNCEMENTS=dr.values.announcements.get,
					SEMI_PERMANENT_RESTRICTIONS=dr.values.semiPermanentRestrictions.get,
					dockstaff=List.empty,
					dockmasters=List.empty,
					apClasses=List.empty,
					uapAppts=List.empty,
					hullCounts=List.empty,
					weather=List.empty,
				)).head
			Future(Ok(Json.toJson(dockReport)))
		})
	})
}
