package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class Discount extends StorableClass(Discount) {
	override object values extends ValuesObject {
		val discountId = new IntFieldValue(self, Discount.fields.discountId)
		val categoryId = new IntFieldValue(self, Discount.fields.categoryId)
		val discountName = new StringFieldValue(self, Discount.fields.discountName)
		val createdOn = new NullableDateTimeFieldValue(self, Discount.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, Discount.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, Discount.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, Discount.fields.updatedBy)
		val alertOnClose = new NullableBooleanFieldValue(self, Discount.fields.alertOnClose)
	}
}

object Discount extends StorableObject[Discount] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "DISCOUNTS"

	object fields extends FieldsObject {
		val discountId = new IntDatabaseField(self, "DISCOUNT_ID")
		val categoryId = new IntDatabaseField(self, "CATEGORY_ID")
		val discountName = new StringDatabaseField(self, "DISCOUNT_NAME", 100)
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val alertOnClose = new NullableBooleanDatabaseField(self, "ALERT_ON_CLOSE")
	}

	def primaryKey: IntDatabaseField = fields.discountId
}