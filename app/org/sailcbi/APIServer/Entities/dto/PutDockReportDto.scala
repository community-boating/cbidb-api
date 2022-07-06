package org.sailcbi.APIServer.Entities.dto

import com.coleji.neptune.API.{ValidationOk, ValidationResult}
import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.Storable.DTOClass
import com.coleji.neptune.Util.GenerateSetDelta
import org.sailcbi.APIServer.Entities.EntityDefinitions._
import play.api.libs.json.{JsValue, Json}

import java.time.{LocalDate, LocalDateTime}

case class PutDockReportDto (
	DOCK_REPORT_ID: Option[Int],
	REPORT_DATE: LocalDate,
	SUNSET_DATETIME: Option[LocalDateTime],
	INCIDENTS_NOTES: Option[String],
	ANNOUNCEMENTS: Option[String],
	SEMI_PERMANENT_RESTRICTIONS: Option[String],
	SIGNED_BY: Option[Int],
	dockstaff: List[PutDockReportStaffDto],
	dockmasters: List[PutDockReportStaffDto],
	var apClasses: List[PutDockReportApClassDto],
	uapAppts: List[PutDockReportUapApptDto],
	var hullCounts: List[PutDockReportHullCountDto],
	weather: List[PutDockReportWeatherDto]
) extends DTOClass[DockReport] {
	override def getId: Option[Int] = DOCK_REPORT_ID

	override def mutateStorableForUpdate(s: DockReport): DockReport = {
		s.update(_.reportDate, REPORT_DATE)
		s.update(_.sunsetDatetime, SUNSET_DATETIME)
		s.update(_.incidentsNotes, INCIDENTS_NOTES)
		s.update(_.announcements, ANNOUNCEMENTS)
		s.update(_.semiPermanentRestrictions, SEMI_PERMANENT_RESTRICTIONS)
		s.update(_.signedBy, SIGNED_BY)
		s
	}

	override def mutateStorableForInsert(s: DockReport):  DockReport = mutateStorableForUpdate(s)

	override def recurse(rc: UnlockedRequestCache, s: DockReport): ValidationResult = {
		// Get existing subobjects
		val (weatherExisting, dockstaffExisting, uapApptsExisting, hullCountsExisting, apClassesExisting) = s.getSubobjects(rc)
		val (weatherExistingDto, dockstaffExistingDto, uapApptsExistingDto, hullCountsExistingDto, apClassesExistingDto) = (
			weatherExisting.map(PutDockReportWeatherDto.apply),
			dockstaffExisting.map(PutDockReportStaffDto.apply),
			uapApptsExisting.map(PutDockReportUapApptDto.apply),
			hullCountsExisting.map(PutDockReportHullCountDto.apply),
			apClassesExisting.map(PutDockReportApClassDto.apply),
		)

		// Compare against incoming, get ids to delete
		val (weatherIdsToDelete, staffIdsToDelete, uapApptIdsToDelete, hullCountIdsToDelete, apClassIdsToDelete) = (
			GenerateSetDelta[PutDockReportWeatherDto](weather.toSet, weatherExistingDto.toSet, d => d.WEATHER_ID.getOrElse(-1).toString)
				.toDestroy.filter(_.WEATHER_ID.isDefined).map(_.WEATHER_ID.get).toList,
			GenerateSetDelta[PutDockReportStaffDto]((dockstaff ++ dockmasters).toSet, dockstaffExistingDto.toSet, d => d.DOCK_REPORT_STAFF_ID.getOrElse(-1).toString)
				.toDestroy.filter(_.DOCK_REPORT_STAFF_ID.isDefined).map(_.DOCK_REPORT_STAFF_ID.get).toList,
			GenerateSetDelta[PutDockReportUapApptDto](uapAppts.toSet, uapApptsExistingDto.toSet, d => d.DOCK_REPORT_APPT_ID.getOrElse(-1).toString)
				.toDestroy.filter(_.DOCK_REPORT_APPT_ID.isDefined).map(_.DOCK_REPORT_APPT_ID.get).toList,
			GenerateSetDelta[PutDockReportHullCountDto](hullCounts.toSet, hullCountsExistingDto.toSet, d => d.DOCK_REPORT_HULL_CT_ID.getOrElse(-1).toString)
				.toDestroy.filter(_.DOCK_REPORT_HULL_CT_ID.isDefined).map(_.DOCK_REPORT_HULL_CT_ID.get).toList,
			GenerateSetDelta[PutDockReportApClassDto](apClasses.toSet, apClassesExistingDto.toSet, d => d.DOCK_REPORT_AP_CLASS_ID.getOrElse(-1).toString)
				.toDestroy.filter(_.DOCK_REPORT_AP_CLASS_ID.isDefined).map(_.DOCK_REPORT_AP_CLASS_ID.get).toList
		)

		// do the dirty deed
		rc.deleteObjectsById(DockReportWeather, weatherIdsToDelete)
		rc.deleteObjectsById(DockReportStaff, staffIdsToDelete)
		rc.deleteObjectsById(DockReportUapAppt, uapApptIdsToDelete)
		rc.deleteObjectsById(DockReportHullCount, hullCountIdsToDelete)
		rc.deleteObjectsById(DockReportApClass, apClassIdsToDelete)

		// put new objects
		for (
			_ <- ValidationResult.combine(dockstaff.map(s => {
				s.DOCK_REPORT_ID = DOCK_REPORT_ID
				s.recurseThroughObject(DockReportStaff, rc).swap.getOrElse(ValidationOk)
			}));
			_ <- ValidationResult.combine(dockmasters.map(s => {
				s.DOCK_REPORT_ID = DOCK_REPORT_ID
				s.recurseThroughObject(DockReportStaff, rc).swap.getOrElse(ValidationOk)
			}));
			_ <- ValidationResult.combine(apClasses.map(s => {
				s.DOCK_REPORT_ID = DOCK_REPORT_ID
				s.recurseThroughObject(DockReportApClass, rc).swap.getOrElse(ValidationOk)
			}));
			_ <- ValidationResult.combine(uapAppts.map(s => {
				s.DOCK_REPORT_ID = DOCK_REPORT_ID
				s.recurseThroughObject(DockReportUapAppt, rc).swap.getOrElse(ValidationOk)
			}));
			_ <- ValidationResult.combine(hullCounts.map(s => {
				s.DOCK_REPORT_ID = DOCK_REPORT_ID
				s.recurseThroughObject(DockReportHullCount, rc).swap.getOrElse(ValidationOk)
			}));
			validationResult <- ValidationResult.combine(weather.map(s => {
				s.DOCK_REPORT_ID = DOCK_REPORT_ID
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
		SIGNED_BY=dr.values.signedBy.peek.flatten,
		dockstaff=List.empty,
		dockmasters=List.empty,
		apClasses=List.empty,
		uapAppts=List.empty,
		hullCounts=List.empty,
		weather=List.empty,
	)

	def applyWithSubObjects(rc: UnlockedRequestCache)(dr: DockReport): PutDockReportDto = {
		val (weather, dockstaff, uapAppts, hullCounts, apClasses) = dr.getSubobjects(rc)
		new PutDockReportDto(
			DOCK_REPORT_ID=Some(dr.values.dockReportId.get),
			REPORT_DATE=dr.values.reportDate.get,
			SUNSET_DATETIME=dr.values.sunsetDatetime.get,
			INCIDENTS_NOTES=dr.values.incidentsNotes.get,
			ANNOUNCEMENTS=dr.values.announcements.get,
			SEMI_PERMANENT_RESTRICTIONS=dr.values.semiPermanentRestrictions.get,
			SIGNED_BY = dr.values.signedBy.get,
			dockstaff=dockstaff.filter(!_.values.dockmasterOnDuty.get).map(PutDockReportStaffDto.apply),
			dockmasters=dockstaff.filter(_.values.dockmasterOnDuty.get).map(PutDockReportStaffDto.apply),
			apClasses=apClasses.map(PutDockReportApClassDto.apply),
			uapAppts=uapAppts.map(PutDockReportUapApptDto.apply),
			hullCounts=hullCounts.map(PutDockReportHullCountDto.apply),
			weather=weather.map(PutDockReportWeatherDto.apply),
		)
	}
}