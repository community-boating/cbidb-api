package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class MembershipsDiscount extends StorableClass(MembershipsDiscount) {
	override object values extends ValuesObject {
		val assignId = new IntFieldValue(self, MembershipsDiscount.fields.assignId)
		val typeId = new IntFieldValue(self, MembershipsDiscount.fields.typeId)
		val instanceId = new IntFieldValue(self, MembershipsDiscount.fields.instanceId)
		val discountAmt = new NullableDoubleFieldValue(self, MembershipsDiscount.fields.discountAmt)
		val createdOn = new NullableDateTimeFieldValue(self, MembershipsDiscount.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, MembershipsDiscount.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, MembershipsDiscount.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, MembershipsDiscount.fields.updatedBy)
		val regCodeRowId = new NullableIntFieldValue(self, MembershipsDiscount.fields.regCodeRowId)
		val arSourceId = new NullableIntFieldValue(self, MembershipsDiscount.fields.arSourceId)
		val arAmount = new NullableDoubleFieldValue(self, MembershipsDiscount.fields.arAmount)
		val autoGrant = new NullableBooleanFieldValue(self, MembershipsDiscount.fields.autoGrant)
		val freeGp = new NullableBooleanFieldValue(self, MembershipsDiscount.fields.freeGp)
		val freeDw = new NullableBooleanFieldValue(self, MembershipsDiscount.fields.freeDw)
	}
}

object MembershipsDiscount extends StorableObject[MembershipsDiscount] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "MEMBERSHIPS_DISCOUNTS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val discountAmt = new NullableDoubleDatabaseField(self, "DISCOUNT_AMT")
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
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