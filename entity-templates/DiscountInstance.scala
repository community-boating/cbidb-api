package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class DiscountInstance extends StorableClass(DiscountInstance) {
	object values extends ValuesObject {
		val instanceId = new IntFieldValue(self, DiscountInstance.fields.instanceId)
		val discountId = new NullableIntFieldValue(self, DiscountInstance.fields.discountId)
		val nameOverride = new NullableStringFieldValue(self, DiscountInstance.fields.nameOverride)
		val startActive = new NullableLocalDateTimeFieldValue(self, DiscountInstance.fields.startActive)
		val endActive = new NullableLocalDateTimeFieldValue(self, DiscountInstance.fields.endActive)
		val universalCode = new NullableStringFieldValue(self, DiscountInstance.fields.universalCode)
		val isCaseSensitive = new NullableBooleanFIeldValue(self, DiscountInstance.fields.isCaseSensitive)
		val createdOn = new NullableLocalDateTimeFieldValue(self, DiscountInstance.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, DiscountInstance.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, DiscountInstance.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, DiscountInstance.fields.updatedBy)
		val emailRegexp = new NullableStringFieldValue(self, DiscountInstance.fields.emailRegexp)
	}
}

object DiscountInstance extends StorableObject[DiscountInstance] {
	val entityName: String = "DISCOUNT_INSTANCES"

	object fields extends FieldsObject {
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val discountId = new NullableIntDatabaseField(self, "DISCOUNT_ID")
		val nameOverride = new NullableStringDatabaseField(self, "NAME_OVERRIDE", 100)
		val startActive = new NullableLocalDateTimeDatabaseField(self, "START_ACTIVE")
		val endActive = new NullableLocalDateTimeDatabaseField(self, "END_ACTIVE")
		val universalCode = new NullableStringDatabaseField(self, "UNIVERSAL_CODE", 100)
		val isCaseSensitive = new NullableBooleanDatabaseField(self, "IS_CASE_SENSITIVE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val emailRegexp = new NullableStringDatabaseField(self, "EMAIL_REGEXP", 500)
	}

	def primaryKey: IntDatabaseField = fields.instanceId
}