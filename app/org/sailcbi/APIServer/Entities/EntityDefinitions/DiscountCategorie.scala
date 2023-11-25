package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class DiscountCategorie extends StorableClass(DiscountCategorie) {
	override object values extends ValuesObject {
		val categoryId = new IntFieldValue(self, DiscountCategorie.fields.categoryId)
		val categoryName = new StringFieldValue(self, DiscountCategorie.fields.categoryName)
		val createdOn = new NullableDateTimeFieldValue(self, DiscountCategorie.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, DiscountCategorie.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, DiscountCategorie.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, DiscountCategorie.fields.updatedBy)
	}
}

object DiscountCategorie extends StorableObject[DiscountCategorie] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "DISCOUNT_CATEGORIES"

	object fields extends FieldsObject {
		val categoryId = new IntDatabaseField(self, "CATEGORY_ID")
		@NullableInDatabase
		val categoryName = new StringDatabaseField(self, "CATEGORY_NAME", 100)
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.categoryId
}