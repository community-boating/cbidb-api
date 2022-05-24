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

	override protected def recurse(rc: UnlockedRequestCache): ValidationResult = {
		val dockstaffValidations = dockstaff.map(s => s.recurseThroughObject(s, DockReportStaff, rc))
		val dockmastersValidations = dockmasters.map(s => s.recurseThroughObject(s, DockReportStaff, rc))
		val apClassesValidations = apClasses.map(s => s.recurseThroughObject(s, DockReportApClass, rc))
		val uapApptsValidations = uapAppts.map(s => s.recurseThroughObject(s, DockReportUapAppt, rc))
		val hullCountsValidations = hullCounts.map(s => s.recurseThroughObject(s, DockReportHullCount, rc))
		val weatherValidations = weather.map(s => s.recurseThroughObject(s, DockReportWeather, rc))
		ValidationOk
	}
}

object PutDockReportDto {
	implicit val format = Json.format[PutDockReportDto]

	def apply(v: JsValue): PutDockReportDto = v.as[PutDockReportDto]
}