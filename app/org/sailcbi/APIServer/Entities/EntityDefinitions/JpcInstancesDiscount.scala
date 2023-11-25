package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class JpcInstancesDiscount extends StorableClass(JpcInstancesDiscount) {
	override object values extends ValuesObject {
		val assignId = new IntFieldValue(self, JpcInstancesDiscount.fields.assignId)
		val classInstanceId = new NullableIntFieldValue(self, JpcInstancesDiscount.fields.classInstanceId)
		val discountInstanceId = new NullableIntFieldValue(self, JpcInstancesDiscount.fields.discountInstanceId)
		val discountAmt = new NullableDoubleFieldValue(self, JpcInstancesDiscount.fields.discountAmt)
		val createdOn = new NullableDateTimeFieldValue(self, JpcInstancesDiscount.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpcInstancesDiscount.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, JpcInstancesDiscount.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, JpcInstancesDiscount.fields.updatedBy)
		val regCodeRowId = new NullableIntFieldValue(self, JpcInstancesDiscount.fields.regCodeRowId)
	}
}

object JpcInstancesDiscount extends StorableObject[JpcInstancesDiscount] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "JPC_INSTANCES_DISCOUNTS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val classInstanceId = new NullableIntDatabaseField(self, "CLASS_INSTANCE_ID")
		val discountInstanceId = new NullableIntDatabaseField(self, "DISCOUNT_INSTANCE_ID")
		val discountAmt = new NullableDoubleDatabaseField(self, "DISCOUNT_AMT")
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val regCodeRowId = new NullableIntDatabaseField(self, "REG_CODE_ROW_ID")
	}

	def primaryKey: IntDatabaseField = fields.assignId
}