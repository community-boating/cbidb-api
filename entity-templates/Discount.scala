package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class Discount extends StorableClass(Discount) {
	object values extends ValuesObject {
		val discountId = new IntFieldValue(self, Discount.fields.discountId)
		val categoryId = new NullableIntFieldValue(self, Discount.fields.categoryId)
		val discountName = new NullableStringFieldValue(self, Discount.fields.discountName)
		val createdOn = new NullableLocalDateTimeFieldValue(self, Discount.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, Discount.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, Discount.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, Discount.fields.updatedBy)
		val alertOnClose = new NullableBooleanFIeldValue(self, Discount.fields.alertOnClose)
	}
}

object Discount extends StorableObject[Discount] {
	val entityName: String = "DISCOUNTS"

	object fields extends FieldsObject {
		val discountId = new IntDatabaseField(self, "DISCOUNT_ID")
		val categoryId = new NullableIntDatabaseField(self, "CATEGORY_ID")
		val discountName = new NullableStringDatabaseField(self, "DISCOUNT_NAME", 100)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val alertOnClose = new NullableBooleanDatabaseField(self, "ALERT_ON_CLOSE")
	}

	def primaryKey: IntDatabaseField = fields.discountId
}