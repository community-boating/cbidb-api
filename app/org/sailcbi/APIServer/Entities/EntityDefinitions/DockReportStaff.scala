package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class DockReportStaff extends StorableClass(DockReportStaff) {
	override object values extends ValuesObject {
		val dockReportStaffId = new IntFieldValue(self, DockReportStaff.fields.dockReportStaffId)
		val dockReportId = new IntFieldValue(self, DockReportStaff.fields.dockReportId)
		val dockmasterOnDuty = new BooleanFieldValue(self, DockReportStaff.fields.dockmasterOnDuty)
		val staffName = new StringFieldValue(self, DockReportStaff.fields.staffName)
		val timeIn = new NullableDateTimeFieldValue(self, DockReportStaff.fields.timeIn)
		val timeOut = new NullableDateTimeFieldValue(self, DockReportStaff.fields.timeOut)
		val createdOn = new NullableDateTimeFieldValue(self, DockReportStaff.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, DockReportStaff.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, DockReportStaff.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, DockReportStaff.fields.updatedBy)
	}
}

object DockReportStaff extends StorableObject[DockReportStaff] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "DOCK_REPORT_STAFF"

	object fields extends FieldsObject {
		val dockReportStaffId = new IntDatabaseField(self, "DOCK_REPORT_STAFF_ID")
		val dockReportId = new IntDatabaseField(self, "DOCK_REPORT_ID")
		val dockmasterOnDuty = new BooleanDatabaseField(self, "DOCKMASTER_ON_DUTY", false)
		val staffName = new StringDatabaseField(self, "STAFF_NAME", 100)
		val timeIn = new NullableDateTimeDatabaseField(self, "TIME_IN")
		val timeOut = new NullableDateTimeDatabaseField(self, "TIME_OUT")
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 100)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 100)
	}

	def primaryKey: IntDatabaseField = fields.dockReportStaffId
}