package org.sailcbi.APIServer.Entities.dto

import com.coleji.neptune.API.{ValidationOk, ValidationResult}
import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.Storable.DTOClass
import org.sailcbi.APIServer.Entities.EntityDefinitions.{DockReport, DockReportApClass, DockReportHullCount, DockReportStaff, DockReportUapAppt, DockReportWeather}
import play.api.libs.json.{JsValue, Json}

import java.time.{LocalDate, LocalDateTime}

case class PutDockReportDto (
	DOCK_REPORT_ID: Option[Int],
	REPORT_DATE: Option[LocalDate],
	SUNSET_DATETIME: Option[LocalDateTime],
	INCIDENTS_NOTES: Option[String],
	ANNOUNCEMENTS: Option[String],
	SEMI_PERMANENT_RESTRICTIONS: Option[String],
	dockstaff: List[PutDockReportStaffDto],
	dockmasters: List[PutDockReportStaffDto],
	apClasses: List[PutDockReportApClassDto],
	uapAppts: List[PutDockReportUapApptDto],
	hullCounts: List[PutDockReportHullCountDto],
	weather: List[PutDockReportWeatherDto]
) extends DTOClass[DockReport] {
	override def getId: Option[Int] = DOCK_REPORT_ID

	override def mutateStorableForUpdate(s: DockReport): DockReport = {
		s.update(_.reportDate, REPORT_DATE)
		s.update(_.sunsetDatetime, SUNSET_DATETIME)
		s.update(_.incidentsNotes, INCIDENTS_NOTES)
		s.update(_.announcements, ANNOUNCEMENTS)
		s.update(_.semiPermanentRestrictions, SEMI_PERMANENT_RESTRICTIONS)
		s
	}

	override def mutateStorableForInsert(s: DockReport):  DockReport = mutateStorableForUpdate(s)

	override def recurse(rc: UnlockedRequestCache): ValidationResult = {
		for (
			_ <- ValidationResult.combine(dockstaff.map(s => {
				//			s.DOCK_REPORT_ID = DOCK_REPORT_ID.get
				s.recurseThroughObject(DockReportStaff, rc).swap.getOrElse(ValidationOk)
			}));
			_ <- ValidationResult.combine(dockmasters.map(s => {
				//			s.DOCK_REPORT_ID = DOCK_REPORT_ID.get
				s.recurseThroughObject(DockReportStaff, rc).swap.getOrElse(ValidationOk)
			}));
			_ <- ValidationResult.combine(apClasses.map(s => {
				//			s.DOCK_REPORT_ID = DOCK_REPORT_ID.get
				s.recurseThroughObject(DockReportApClass, rc).swap.getOrElse(ValidationOk)
			}));
			_ <- ValidationResult.combine(uapAppts.map(s => {
				//			s.DOCK_REPORT_ID = DOCK_REPORT_ID.get
				s.recurseThroughObject(DockReportUapAppt, rc).swap.getOrElse(ValidationOk)
			}));
			_ <- ValidationResult.combine(hullCounts.map(s => {
				//			s.DOCK_REPORT_ID = DOCK_REPORT_ID.get
				s.recurseThroughObject(DockReportHullCount, rc).swap.getOrElse(ValidationOk)
			}));
			validationResult <- ValidationResult.combine(weather.map(s => {
				println("doing weather")
				s.DOCK_REPORT_ID = DOCK_REPORT_ID.get
				s.recurseThroughObject(DockReportWeather, rc).swap.getOrElse(ValidationOk)
			}))
		) yield validationResult
	}
}

object PutDockReportDto {
	implicit val format = Json.format[PutDockReportDto]

	def apply(v: JsValue): PutDockReportDto = v.as[PutDockReportDto]

	def apply(dr: DockReport): PutDockReportDto = new PutDockReportDto(
		DOCK_REPORT_ID=Some(dr.values.dockReportId.get),
		REPORT_DATE=dr.values.reportDate.get,
		SUNSET_DATETIME=dr.values.sunsetDatetime.peek.flatten,
		INCIDENTS_NOTES=dr.values.incidentsNotes.peek.flatten,
		ANNOUNCEMENTS=dr.values.announcements.peek.flatten,
		SEMI_PERMANENT_RESTRICTIONS=dr.values.semiPermanentRestrictions.peek.flatten,
		dockstaff=List.empty,
		dockmasters=List.empty,
		apClasses=List.empty,
		uapAppts=List.empty,
		hullCounts=List.empty,
		weather=List.empty,
	)
}