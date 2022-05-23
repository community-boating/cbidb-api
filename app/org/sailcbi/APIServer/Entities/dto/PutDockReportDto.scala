package org.sailcbi.APIServer.Entities.dto

import com.coleji.neptune.API.{ValidationOk, ValidationResult}
import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.Storable.DTOClass
import org.sailcbi.APIServer.Entities.EntityDefinitions.{DockReport, DockReportApClass, DockReportHullCount, DockReportStaff, DockReportUapAppt, DockReportWeather}
import play.api.libs.json.{JsValue, Json}

import java.time.{LocalDate, LocalDateTime}

case class PutDockReportDto (
	dockReportId: Option[Int],
	reportDate: Option[LocalDate],
	sunsetDatetime: Option[LocalDateTime],
	incidentsNotes: Option[String],
	announcements: Option[String],
	semiPermanentRestrictions: Option[String],
	staff: List[PutDockReportStaffDto],
	apClasses: List[PutDockReportApClassDto],
	uapAppts: List[PutDockReportUapApptDto],
	hullCounts: List[PutDockReportHullCountDto],
	weather: List[PutDockReportWeatherDto]
) extends DTOClass[DockReport] {
	override def getId: Option[Int] = dockReportId

	override def mutateStorableForUpdate(s: DockReport): DockReport = {
		s.update(_.reportDate, reportDate)
		s.update(_.sunsetDatetime, sunsetDatetime)
		s.update(_.incidentsNotes, incidentsNotes)
		s.update(_.announcements, announcements)
		s.update(_.semiPermanentRestrictions, semiPermanentRestrictions)
		s
	}

	override def mutateStorableForInsert(s: DockReport):  DockReport = mutateStorableForUpdate(s)

	override protected def recurse(rc: UnlockedRequestCache): ValidationResult = {
		val staffValidations = staff.map(s => s.recurseThroughObject(s, DockReportStaff, rc))
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