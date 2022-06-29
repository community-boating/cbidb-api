package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class DockReport extends StorableClass(DockReport) {
	object values extends ValuesObject {
		val dockReportId = new IntFieldValue(self, DockReport.fields.dockReportId)
		val reportDate = new DateFieldValue(self, DockReport.fields.reportDate)
		val sunsetDatetime = new NullableDateTimeFieldValue(self, DockReport.fields.sunsetDatetime)
		val incidentsNotes = new NullableStringFieldValue(self, DockReport.fields.incidentsNotes)
		val announcements = new NullableStringFieldValue(self, DockReport.fields.announcements)
		val semiPermanentRestrictions = new NullableStringFieldValue(self, DockReport.fields.semiPermanentRestrictions)
		val signedBy = new NullableIntFieldValue(self, DockReport.fields.signedBy)
	}

	def getSubobjects(rc: UnlockedRequestCache): (List[DockReportWeather], List[DockReportStaff], List[DockReportUapAppt], List[DockReportHullCount], List[DockReportApClass]) ={
		val weather = rc.getObjectsByFilters(DockReportWeather, List(DockReportWeather.fields.dockReportId.alias.equalsConstant(values.dockReportId.get)), Set.empty)
		val dockstaff = rc.getObjectsByFilters(DockReportStaff, List(DockReportStaff.fields.dockReportId.alias.equalsConstant(values.dockReportId.get)), Set.empty)
		val uapAppts = rc.getObjectsByFilters(DockReportUapAppt, List(DockReportUapAppt.fields.dockReportId.alias.equalsConstant(values.dockReportId.get)), Set.empty)
		val hullCounts = rc.getObjectsByFilters(DockReportHullCount, List(DockReportHullCount.fields.dockReportId.alias.equalsConstant(values.dockReportId.get)), Set.empty)
		val apClasses = rc.getObjectsByFilters(DockReportApClass, List(DockReportApClass.fields.dockReportId.alias.equalsConstant(values.dockReportId.get)), Set.empty)
		(weather, dockstaff, uapAppts, hullCounts, apClasses)
	}

	def deleteSubobjects(rc: UnlockedRequestCache): Unit = {
		val (weather, dockstaff, uapAppts, hullCounts, apClasses) = getSubobjects(rc)
		val weatherIds = weather.map(_.values.weatherId.get)
		val dockstaffIds = dockstaff.map(_.values.dockReportStaffId.get)
		val uapApptIds = uapAppts.map(_.values.dockReportApptId.get)
		val hullCountIds = hullCounts.map(_.values.dockReportHullCtId.get)
		val apClassIds = apClasses.map(_.values.dockReportApClassId.get)
		rc.deleteObjectsById(DockReportWeather, weatherIds)
		rc.deleteObjectsById(DockReportStaff, dockstaffIds)
		rc.deleteObjectsById(DockReportUapAppt, uapApptIds)
		rc.deleteObjectsById(DockReportHullCount, hullCountIds)
		rc.deleteObjectsById(DockReportApClass, apClassIds)
	}
}

object DockReport extends StorableObject[DockReport] {
	val entityName: String = "DOCK_REPORTS"

	object fields extends FieldsObject {
		val dockReportId = new IntDatabaseField(self, "DOCK_REPORT_ID")
		val reportDate = new DateDatabaseField(self, "REPORT_DATE")
		val sunsetDatetime = new NullableDateTimeDatabaseField(self, "SUNSET_DATETIME")
		val incidentsNotes = new NullableStringDatabaseField(self, "INCIDENTS_NOTES", 4000)
		val announcements = new NullableStringDatabaseField(self, "ANNOUNCEMENTS", 4000)
		val semiPermanentRestrictions = new NullableStringDatabaseField(self, "SEMI_PERMANENT_RESTRICTIONS", 4000)
		val signedBy = new NullableIntDatabaseField(self, "SIGNED_BY")
	}

	def primaryKey: IntDatabaseField = fields.dockReportId
}