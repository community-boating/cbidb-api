package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class DiscountInstanceLevel extends StorableClass(DiscountInstanceLevel) {
	object values extends ValuesObject {
		val levelId = new IntFieldValue(self, DiscountInstanceLevel.fields.levelId)
		val instanceId = new NullableIntFieldValue(self, DiscountInstanceLevel.fields.instanceId)
		val discountAmt = new NullableDoubleFieldValue(self, DiscountInstanceLevel.fields.discountAmt)
		val createdOn = new NullableLocalDateTimeFieldValue(self, DiscountInstanceLevel.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, DiscountInstanceLevel.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, DiscountInstanceLevel.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, DiscountInstanceLevel.fields.updatedBy)
	}
}

object DiscountInstanceLevel extends StorableObject[DiscountInstanceLevel] {
	val entityName: String = "DISCOUNT_INSTANCE_LEVELS"

	object fields extends FieldsObject {
		val levelId = new IntDatabaseField(self, "LEVEL_ID")
		val instanceId = new NullableIntDatabaseField(self, "INSTANCE_ID")
		val discountAmt = new NullableDoubleDatabaseField(self, "DISCOUNT_AMT")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.levelId
}