package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class DockReport extends StorableClass(DockReport) {
	object values extends ValuesObject {
		val dockReportId = new IntFieldValue(self, DockReport.fields.dockReportId)
		val reportDate = new NullableDateFieldValue(self, DockReport.fields.reportDate)
		val sunsetDatetime = new NullableDateTimeFieldValue(self, DockReport.fields.sunsetDatetime)
		val incidentsNotes = new NullableStringFieldValue(self, DockReport.fields.incidentsNotes)
		val announcements = new NullableStringFieldValue(self, DockReport.fields.announcements)
		val semiPermanentRestrictions = new NullableStringFieldValue(self, DockReport.fields.semiPermanentRestrictions)
	}
}

object DockReport extends StorableObject[DockReport] {
	val entityName: String = "DOCK_REPORTS"

	object fields extends FieldsObject {
		val dockReportId = new IntDatabaseField(self, "DOCK_REPORT_ID")
		val reportDate = new NullableDateDatabaseField(self, "REPORT_DATE")
		val sunsetDatetime = new NullableDateTimeDatabaseField(self, "SUNSET_DATETIME")
		val incidentsNotes = new NullableStringDatabaseField(self, "INCIDENTS_NOTES", 4000)
		val announcements = new NullableStringDatabaseField(self, "ANNOUNCEMENTS", 4000)
		val semiPermanentRestrictions = new NullableStringDatabaseField(self, "SEMI_PERMANENT_RESTRICTIONS", 4000)
	}

	def primaryKey: IntDatabaseField = fields.dockReportId
}