package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ApDockRptStaff extends StorableClass(ApDockRptStaff) {
	object values extends ValuesObject {
		val drStaffId = new IntFieldValue(self, ApDockRptStaff.fields.drStaffId)
		val drId = new NullableIntFieldValue(self, ApDockRptStaff.fields.drId)
		val name = new NullableStringFieldValue(self, ApDockRptStaff.fields.name)
		val isDockmaster = new NullableBooleanFIeldValue(self, ApDockRptStaff.fields.isDockmaster)
		val punchIn = new NullableStringFieldValue(self, ApDockRptStaff.fields.punchIn)
		val punchOut = new NullableStringFieldValue(self, ApDockRptStaff.fields.punchOut)
		val createdOn = new NullableLocalDateTimeFieldValue(self, ApDockRptStaff.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ApDockRptStaff.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, ApDockRptStaff.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ApDockRptStaff.fields.updatedBy)
	}
}

object ApDockRptStaff extends StorableObject[ApDockRptStaff] {
	val entityName: String = "AP_DOCK_RPT_STAFF"

	object fields extends FieldsObject {
		val drStaffId = new IntDatabaseField(self, "DR_STAFF_ID")
		val drId = new NullableIntDatabaseField(self, "DR_ID")
		val name = new NullableStringDatabaseField(self, "NAME", 100)
		val isDockmaster = new NullableBooleanDatabaseField(self, "IS_DOCKMASTER")
		val punchIn = new NullableStringDatabaseField(self, "PUNCH_IN", 10)
		val punchOut = new NullableStringDatabaseField(self, "PUNCH_OUT", 10)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.drStaffId
}