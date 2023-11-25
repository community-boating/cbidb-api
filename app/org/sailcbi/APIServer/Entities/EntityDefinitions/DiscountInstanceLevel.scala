package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class DiscountInstanceLevel extends StorableClass(DiscountInstanceLevel) {
	override object values extends ValuesObject {
		val levelId = new IntFieldValue(self, DiscountInstanceLevel.fields.levelId)
		val instanceId = new NullableIntFieldValue(self, DiscountInstanceLevel.fields.instanceId)
		val discountAmt = new NullableDoubleFieldValue(self, DiscountInstanceLevel.fields.discountAmt)
		val createdOn = new DateTimeFieldValue(self, DiscountInstanceLevel.fields.createdOn)
		val createdBy = new StringFieldValue(self, DiscountInstanceLevel.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, DiscountInstanceLevel.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, DiscountInstanceLevel.fields.updatedBy)
	}
}

object DiscountInstanceLevel extends StorableObject[DiscountInstanceLevel] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "DISCOUNT_INSTANCE_LEVELS"

	object fields extends FieldsObject {
		val levelId = new IntDatabaseField(self, "LEVEL_ID")
		val instanceId = new NullableIntDatabaseField(self, "INSTANCE_ID")
		val discountAmt = new NullableDoubleDatabaseField(self, "DISCOUNT_AMT")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.levelId
}