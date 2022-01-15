package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FoItemCategorie extends StorableClass(FoItemCategorie) {
	object values extends ValuesObject {
		val categoryId = new IntFieldValue(self, FoItemCategorie.fields.categoryId)
		val categoryName = new NullableStringFieldValue(self, FoItemCategorie.fields.categoryName)
		val createdOn = new NullableLocalDateTimeFieldValue(self, FoItemCategorie.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, FoItemCategorie.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, FoItemCategorie.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, FoItemCategorie.fields.updatedBy)
	}
}

object FoItemCategorie extends StorableObject[FoItemCategorie] {
	val entityName: String = "FO_ITEM_CATEGORIES"

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