package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class MembershipsDiscount extends StorableClass(MembershipsDiscount) {
	object values extends ValuesObject {
		val assignId = new IntFieldValue(self, MembershipsDiscount.fields.assignId)
		val typeId = new IntFieldValue(self, MembershipsDiscount.fields.typeId)
		val instanceId = new NullableIntFieldValue(self, MembershipsDiscount.fields.instanceId)
		val discountAmt = new NullableDoubleFieldValue(self, MembershipsDiscount.fields.discountAmt)
		val createdOn = new NullableLocalDateTimeFieldValue(self, MembershipsDiscount.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, MembershipsDiscount.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, MembershipsDiscount.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, MembershipsDiscount.fields.updatedBy)
		val regCodeRowId = new NullableIntFieldValue(self, MembershipsDiscount.fields.regCodeRowId)
		val arSourceId = new NullableIntFieldValue(self, MembershipsDiscount.fields.arSourceId)
		val arAmount = new NullableDoubleFieldValue(self, MembershipsDiscount.fields.arAmount)
		val autoGrant = new NullableBooleanFIeldValue(self, MembershipsDiscount.fields.autoGrant)
		val freeGp = new NullableBooleanFIeldValue(self, MembershipsDiscount.fields.freeGp)
		val freeDw = new NullableBooleanFIeldValue(self, MembershipsDiscount.fields.freeDw)
	}
}

object MembershipsDiscount extends StorableObject[MembershipsDiscount] {
	val entityName: String = "MEMBERSHIPS_DISCOUNTS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val instanceId = new NullableIntDatabaseField(self, "INSTANCE_ID")
		val discountAmt = new NullableDoubleDatabaseField(self, "DISCOUNT_AMT")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val regCodeRowId = new NullableIntDatabaseField(self, "REG_CODE_ROW_ID")
		val arSourceId = new NullableIntDatabaseField(self, "AR_SOURCE_ID")
		val arAmount = new NullableDoubleDatabaseField(self, "AR_AMOUNT")
		val autoGrant = new NullableBooleanDatabaseField(self, "AUTO_GRANT")
		val freeGp = new NullableBooleanDatabaseField(self, "FREE_GP")
		val freeDw = new NullableBooleanDatabaseField(self, "FREE_DW")
	}

	def primaryKey: IntDatabaseField = fields.assignId
}