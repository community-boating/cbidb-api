package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class DiscountCategorie extends StorableClass(DiscountCategorie) {
	object values extends ValuesObject {
		val categoryId = new IntFieldValue(self, DiscountCategorie.fields.categoryId)
		val categoryName = new NullableStringFieldValue(self, DiscountCategorie.fields.categoryName)
		val createdOn = new NullableLocalDateTimeFieldValue(self, DiscountCategorie.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, DiscountCategorie.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, DiscountCategorie.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, DiscountCategorie.fields.updatedBy)
	}
}

object DiscountCategorie extends StorableObject[DiscountCategorie] {
	val entityName: String = "DISCOUNT_CATEGORIES"

	object fields extends FieldsObject {
		val categoryId = new IntDatabaseField(self, "CATEGORY_ID")
		val categoryName = new NullableStringDatabaseField(self, "CATEGORY_NAME", 100)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.categoryId
}