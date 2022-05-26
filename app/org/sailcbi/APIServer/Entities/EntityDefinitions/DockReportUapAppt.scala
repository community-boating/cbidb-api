package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class DockReportUapAppt extends StorableClass(DockReportUapAppt) {
	object values extends ValuesObject {
		val dockReportApptId = new IntFieldValue(self, DockReportUapAppt.fields.dockReportApptId)
		val dockReportId = new IntFieldValue(self, DockReportUapAppt.fields.dockReportId)
		val apptDatetime = new DateTimeFieldValue(self, DockReportUapAppt.fields.apptDatetime)
		val apptType = new StringFieldValue(self, DockReportUapAppt.fields.apptType)
		val participantName = new StringFieldValue(self, DockReportUapAppt.fields.participantName)
		val boatTypeId = new IntFieldValue(self, DockReportUapAppt.fields.boatTypeId)
		val instructorName = new StringFieldValue(self, DockReportUapAppt.fields.instructorName)
	}
}

object DockReportUapAppt extends StorableObject[DockReportUapAppt] {
	val entityName: String = "DOCK_REPORT_UAP_APPTS"

	object fields extends FieldsObject {
		val dockReportApptId = new IntDatabaseField(self, "DOCK_REPORT_APPT_ID")
		val dockReportId = new IntDatabaseField(self, "DOCK_REPORT_ID")
		val apptDatetime = new DateTimeDatabaseField(self, "APPT_DATETIME")
		val apptType = new StringDatabaseField(self, "APPT_TYPE", 20)
		val participantName = new StringDatabaseField(self, "PARTICIPANT_NAME", 150)
		val boatTypeId = new IntDatabaseField(self, "BOAT_TYPE_ID")
		val instructorName = new StringDatabaseField(self, "INSTRUCTOR_NAME", 100)
	}

	def primaryKey: IntDatabaseField = fields.dockReportApptId
}