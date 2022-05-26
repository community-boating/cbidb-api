package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class DockReportStaff extends StorableClass(DockReportStaff) {
	object values extends ValuesObject {
		val dockReportStaffId = new IntFieldValue(self, DockReportStaff.fields.dockReportStaffId)
		val dockReportId = new IntFieldValue(self, DockReportStaff.fields.dockReportId)
		val dockmasterOnDuty = new BooleanFieldValue(self, DockReportStaff.fields.dockmasterOnDuty)
		val staffName = new NullableStringFieldValue(self, DockReportStaff.fields.staffName)
		val timeIn = new NullableDateTimeFieldValue(self, DockReportStaff.fields.timeIn)
		val timeOut = new NullableDateTimeFieldValue(self, DockReportStaff.fields.timeOut)

	}
}

object DockReportStaff extends StorableObject[DockReportStaff] {
	val entityName: String = "DOCK_REPORT_STAFF"

	object fields extends FieldsObject {
		val dockReportStaffId = new IntDatabaseField(self, "DOCK_REPORT_STAFF_ID")
		val dockReportId = new IntDatabaseField(self, "DOCK_REPORT_ID")
		val dockmasterOnDuty = new BooleanDatabaseField(self, "DOCKMASTER_ON_DUTY")
		val staffName = new NullableStringDatabaseField(self, "STAFF_NAME", 100)
		val timeIn = new NullableDateTimeDatabaseField(self, "TIME_IN")
		val timeOut = new NullableDateTimeDatabaseField(self, "TIME_OUT")

	}

	def primaryKey: IntDatabaseField = fields.dockReportStaffId
}