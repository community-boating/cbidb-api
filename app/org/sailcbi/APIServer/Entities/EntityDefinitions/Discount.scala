package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._

class Discount extends StorableClass(Discount) {
	object values extends ValuesObject {
		val discountId = new IntFieldValue(self, Discount.fields.discountId)
		val categoryId = new IntFieldValue(self, Discount.fields.categoryId)
		val discountName = new StringFieldValue(self, Discount.fields.discountName)
		val alertOnClose = new BooleanFieldValue(self, Discount.fields.alertOnClose)
	}
}

object Discount extends StorableObject[Discount] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "DISCOUNTS"

	object fields extends FieldsObject {
		val discountId = new IntDatabaseField(self, "DISCOUNT_ID")
		val categoryId = new IntDatabaseField(self, "CATEGORY_ID")
		val discountName = new StringDatabaseField(self, "DISCOUNT_NAME", 100)
		val alertOnClose = new BooleanDatabaseField(self, "ALERT_ON_CLOSE", true)
	}

	def primaryKey: IntDatabaseField = fields.discountId
}