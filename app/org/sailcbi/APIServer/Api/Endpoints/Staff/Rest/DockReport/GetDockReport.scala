package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.DockReport

import com.coleji.neptune.API.RestController
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Entities.EntityDefinitions.{DockReport, DockReportApClass, DockReportHullCount, DockReportStaff, DockReportUapAppt, DockReportWeather}
import org.sailcbi.APIServer.Entities.dto.{PutDockReportApClassDto, PutDockReportDto, PutDockReportHullCountDto, PutDockReportStaffDto, PutDockReportUapApptDto, PutDockReportWeatherDto}
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
				.map(dr => {
					val dockRptId = dr.values.dockReportId.get
					val weather = rc.getObjectsByFilters(DockReportWeather, List(DockReportWeather.fields.dockReportId.alias.equalsConstant(dockRptId)), Set.empty)
					val dockstaff = rc.getObjectsByFilters(DockReportStaff, List(DockReportStaff.fields.dockReportId.alias.equalsConstant(dockRptId)), Set.empty)
					val uapAppts = rc.getObjectsByFilters(DockReportUapAppt, List(DockReportUapAppt.fields.dockReportId.alias.equalsConstant(dockRptId)), Set.empty)
					val hullCounts = rc.getObjectsByFilters(DockReportHullCount, List(DockReportHullCount.fields.dockReportId.alias.equalsConstant(dockRptId)), Set.empty)
					val apClasses = rc.getObjectsByFilters(DockReportApClass, List(DockReportApClass.fields.dockReportId.alias.equalsConstant(dockRptId)), Set.empty)
					new PutDockReportDto(
						DOCK_REPORT_ID=Some(dr.values.dockReportId.get),
						REPORT_DATE=dr.values.reportDate.get,
						SUNSET_DATETIME=dr.values.sunsetDatetime.get,
						INCIDENTS_NOTES=dr.values.incidentsNotes.get,
						ANNOUNCEMENTS=dr.values.announcements.get,
						SEMI_PERMANENT_RESTRICTIONS=dr.values.semiPermanentRestrictions.get,
						dockstaff=dockstaff.filter(!_.values.dockmasterOnDuty.get).map(PutDockReportStaffDto.apply),
						dockmasters=dockstaff.filter(_.values.dockmasterOnDuty.get).map(PutDockReportStaffDto.apply),
						apClasses=apClasses.map(PutDockReportApClassDto.apply),
						uapAppts=uapAppts.map(PutDockReportUapApptDto.apply),
						hullCounts=hullCounts.map(PutDockReportHullCountDto.apply),
						weather=weather.map(PutDockReportWeatherDto.apply),
					)
				}).headOption

			dockReport match {
				case Some(dr) => Future(Ok(Json.toJson(dockReport)))
				case None => {
					val newDockReport = new DockReport
					newDockReport.values.reportDate.update(Some(LocalDate.now))
					// copy yesterday's values
					getByFilters(rc, List(DockReport.fields.reportDate.alias.isDateConstant(LocalDate.now.minusDays(1))), Set.empty).headOption.foreach(yesterday => {
						newDockReport.values.semiPermanentRestrictions.update(yesterday.values.semiPermanentRestrictions.get)
					})
					rc.commitObjectToDatabase(newDockReport)
					Future(Ok(Json.toJson(PutDockReportDto(newDockReport))))
				}
			}

		})
	})
}
