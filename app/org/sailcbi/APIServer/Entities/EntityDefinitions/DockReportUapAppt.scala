package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._

class DockReportUapAppt extends StorableClass(DockReportUapAppt) {
	object values extends ValuesObject {
		val dockReportApptId = new IntFieldValue(self, DockReportUapAppt.fields.dockReportApptId)
		val dockReportId = new IntFieldValue(self, DockReportUapAppt.fields.dockReportId)
		val apptDatetime = new NullableDateTimeFieldValue(self, DockReportUapAppt.fields.apptDatetime)
		val apptType = new NullableStringFieldValue(self, DockReportUapAppt.fields.apptType)
		val participantName = new StringFieldValue(self, DockReportUapAppt.fields.participantName)
		val boatTypeId = new NullableIntFieldValue(self, DockReportUapAppt.fields.boatTypeId)
		val instructorName = new NullableStringFieldValue(self, DockReportUapAppt.fields.instructorName)
		val hoyer = new NullableBooleanFieldValue(self, DockReportUapAppt.fields.hoyer)
	}
}

object DockReportUapAppt extends StorableObject[DockReportUapAppt] {
	val entityName: String = "DOCK_REPORT_UAP_APPTS"

	object fields extends FieldsObject {
		val dockReportApptId = new IntDatabaseField(self, "DOCK_REPORT_APPT_ID")
		val dockReportId = new IntDatabaseField(self, "DOCK_REPORT_ID")
		val apptDatetime = new NullableDateTimeDatabaseField(self, "APPT_DATETIME")
		val apptType = new NullableStringDatabaseField(self, "APPT_TYPE", 20)
		val participantName = new StringDatabaseField(self, "PARTICIPANT_NAME", 150)
		val boatTypeId = new NullableIntDatabaseField(self, "BOAT_TYPE_ID")
		val instructorName = new NullableStringDatabaseField(self, "INSTRUCTOR_NAME", 100)
		val hoyer = new NullableBooleanDatabaseField(self, "HOYER")
	}

	def primaryKey: IntDatabaseField = fields.dockReportApptId
}