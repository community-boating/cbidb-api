package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class DiscountInstance extends StorableClass(DiscountInstance) {
	override object references extends ReferencesObject {
		val discount = new Initializable[Discount]
	}

	object values extends ValuesObject {
		val instanceId = new IntFieldValue(self, DiscountInstance.fields.instanceId)
		val discountId = new IntFieldValue(self, DiscountInstance.fields.discountId)
		val nameOverride = new NullableStringFieldValue(self, DiscountInstance.fields.nameOverride)
		val startActive = new NullableDateTimeFieldValue(self, DiscountInstance.fields.startActive)
		val endActive = new NullableDateTimeFieldValue(self, DiscountInstance.fields.endActive)
		val universalCode = new NullableStringFieldValue(self, DiscountInstance.fields.universalCode)
		val isCaseSensitive = new BooleanFieldValue(self, DiscountInstance.fields.isCaseSensitive)
		val emailRegexp = new NullableStringFieldValue(self, DiscountInstance.fields.emailRegexp)
	}
}

object DiscountInstance extends StorableObject[DiscountInstance] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "DISCOUNT_INSTANCES"

	object fields extends FieldsObject {
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val discountId = new IntDatabaseField(self, "DISCOUNT_ID")
		val nameOverride = new NullableStringDatabaseField(self, "NAME_OVERRIDE", 100)
		val startActive = new NullableDateTimeDatabaseField(self, "START_ACTIVE")
		val endActive = new NullableDateTimeDatabaseField(self, "END_ACTIVE")
		val universalCode = new NullableStringDatabaseField(self, "UNIVERSAL_CODE", 100)
		val isCaseSensitive = new BooleanDatabaseField(self, "IS_CASE_SENSITIVE", true)
		val emailRegexp = new NullableStringDatabaseField(self, "EMAIL_REGEXP", 500)
	}

	def primaryKey: IntDatabaseField = fields.instanceId
}