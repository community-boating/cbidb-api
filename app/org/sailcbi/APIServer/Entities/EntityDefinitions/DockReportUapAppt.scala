package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class DockReportUapAppt extends StorableClass(DockReportUapAppt) {
	override object values extends ValuesObject {
		val dockReportApptId = new IntFieldValue(self, DockReportUapAppt.fields.dockReportApptId)
		val dockReportId = new IntFieldValue(self, DockReportUapAppt.fields.dockReportId)
		val apptDatetime = new DateTimeFieldValue(self, DockReportUapAppt.fields.apptDatetime)
		val apptType = new StringFieldValue(self, DockReportUapAppt.fields.apptType)
		val participantName = new NullableStringFieldValue(self, DockReportUapAppt.fields.participantName)
		val boatTypeId = new NullableIntFieldValue(self, DockReportUapAppt.fields.boatTypeId)
		val instructorName = new NullableStringFieldValue(self, DockReportUapAppt.fields.instructorName)
		val createdOn = new NullableDateTimeFieldValue(self, DockReportUapAppt.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, DockReportUapAppt.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, DockReportUapAppt.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, DockReportUapAppt.fields.updatedBy)
		val hoyer = new NullableBooleanFieldValue(self, DockReportUapAppt.fields.hoyer)
	}
}

object DockReportUapAppt extends StorableObject[DockReportUapAppt] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "DOCK_REPORT_UAP_APPTS"

	object fields extends FieldsObject {
		val dockReportApptId = new IntDatabaseField(self, "DOCK_REPORT_APPT_ID")
		val dockReportId = new IntDatabaseField(self, "DOCK_REPORT_ID")
		@NullableInDatabase
		val apptDatetime = new DateTimeDatabaseField(self, "APPT_DATETIME")
		@NullableInDatabase
		val apptType = new StringDatabaseField(self, "APPT_TYPE", 50)
		val participantName = new NullableStringDatabaseField(self, "PARTICIPANT_NAME", 150)
		val boatTypeId = new NullableIntDatabaseField(self, "BOAT_TYPE_ID")
		val instructorName = new NullableStringDatabaseField(self, "INSTRUCTOR_NAME", 100)
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 100)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 100)
		val hoyer = new NullableBooleanDatabaseField(self, "HOYER")
	}

	def primaryKey: IntDatabaseField = fields.dockReportApptId
}