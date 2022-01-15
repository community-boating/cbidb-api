package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ApDockRptClosure extends StorableClass(ApDockRptClosure) {
	object values extends ValuesObject {
		val closureId = new IntFieldValue(self, ApDockRptClosure.fields.closureId)
		val drId = new NullableIntFieldValue(self, ApDockRptClosure.fields.drId)
		val closeTime = new NullableStringFieldValue(self, ApDockRptClosure.fields.closeTime)
		val reopenTime = new NullableStringFieldValue(self, ApDockRptClosure.fields.reopenTime)
		val createdOn = new NullableLocalDateTimeFieldValue(self, ApDockRptClosure.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ApDockRptClosure.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, ApDockRptClosure.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ApDockRptClosure.fields.updatedBy)
	}
}

object ApDockRptClosure extends StorableObject[ApDockRptClosure] {
	val entityName: String = "AP_DOCK_RPT_CLOSURES"

	object fields extends FieldsObject {
		val closureId = new IntDatabaseField(self, "CLOSURE_ID")
		val drId = new NullableIntDatabaseField(self, "DR_ID")
		val closeTime = new NullableStringDatabaseField(self, "CLOSE_TIME", 10)
		val reopenTime = new NullableStringDatabaseField(self, "REOPEN_TIME", 10)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.closureId
}