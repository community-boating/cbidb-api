package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class DockReport extends StorableClass(DockReport) {
	override object values extends ValuesObject {
		val dockReportId = new IntFieldValue(self, DockReport.fields.dockReportId)
		val reportDate = new DateTimeFieldValue(self, DockReport.fields.reportDate)
		val sunsetDatetime = new NullableDateTimeFieldValue(self, DockReport.fields.sunsetDatetime)
		val incidentsNotes = new NullableStringFieldValue(self, DockReport.fields.incidentsNotes)
		val announcements = new NullableStringFieldValue(self, DockReport.fields.announcements)
		val semiPermanentRestrictions = new NullableStringFieldValue(self, DockReport.fields.semiPermanentRestrictions)
		val createdOn = new NullableDateTimeFieldValue(self, DockReport.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, DockReport.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, DockReport.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, DockReport.fields.updatedBy)
		val signedBy = new NullableDoubleFieldValue(self, DockReport.fields.signedBy)
	}
}

object DockReport extends StorableObject[DockReport] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "DOCK_REPORTS"

	object fields extends FieldsObject {
		val dockReportId = new IntDatabaseField(self, "DOCK_REPORT_ID")
		val reportDate = new DateTimeDatabaseField(self, "REPORT_DATE")
		val sunsetDatetime = new NullableDateTimeDatabaseField(self, "SUNSET_DATETIME")
		val incidentsNotes = new NullableStringDatabaseField(self, "INCIDENTS_NOTES", 4000)
		val announcements = new NullableStringDatabaseField(self, "ANNOUNCEMENTS", 4000)
		val semiPermanentRestrictions = new NullableStringDatabaseField(self, "SEMI_PERMANENT_RESTRICTIONS", 4000)
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 100)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 100)
		val signedBy = new NullableDoubleDatabaseField(self, "SIGNED_BY")
	}

	def primaryKey: IntDatabaseField = fields.dockReportId
}