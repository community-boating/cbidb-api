package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FoAppliedGc extends StorableClass(FoAppliedGc) {
	object values extends ValuesObject {
		val rowId = new IntFieldValue(self, FoAppliedGc.fields.rowId)
		val closeId = new NullableIntFieldValue(self, FoAppliedGc.fields.closeId)
		val certId = new NullableIntFieldValue(self, FoAppliedGc.fields.certId)
		val value = new NullableDoubleFieldValue(self, FoAppliedGc.fields.value)
		val createdOn = new NullableLocalDateTimeFieldValue(self, FoAppliedGc.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, FoAppliedGc.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, FoAppliedGc.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, FoAppliedGc.fields.updatedBy)
		val pmAssignId = new NullableIntFieldValue(self, FoAppliedGc.fields.pmAssignId)
		val orderId = new NullableIntFieldValue(self, FoAppliedGc.fields.orderId)
		val voidCloseId = new NullableIntFieldValue(self, FoAppliedGc.fields.voidCloseId)
	}
}

object FoAppliedGc extends StorableObject[FoAppliedGc] {
	val entityName: String = "FO_APPLIED_GC"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val certId = new NullableIntDatabaseField(self, "CERT_ID")
		val value = new NullableDoubleDatabaseField(self, "VALUE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val pmAssignId = new NullableIntDatabaseField(self, "PM_ASSIGN_ID")
		val orderId = new NullableIntDatabaseField(self, "ORDER_ID")
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}