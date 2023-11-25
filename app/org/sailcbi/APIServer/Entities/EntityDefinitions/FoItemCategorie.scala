package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class FoItemCategorie extends StorableClass(FoItemCategorie) {
	override object values extends ValuesObject {
		val categoryId = new IntFieldValue(self, FoItemCategorie.fields.categoryId)
		val categoryName = new StringFieldValue(self, FoItemCategorie.fields.categoryName)
		val createdOn = new DateTimeFieldValue(self, FoItemCategorie.fields.createdOn)
		val createdBy = new StringFieldValue(self, FoItemCategorie.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, FoItemCategorie.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, FoItemCategorie.fields.updatedBy)
	}
}

object FoItemCategorie extends StorableObject[FoItemCategorie] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "FO_ITEM_CATEGORIES"

	object fields extends FieldsObject {
		val categoryId = new IntDatabaseField(self, "CATEGORY_ID")
		@NullableInDatabase
		val categoryName = new StringDatabaseField(self, "CATEGORY_NAME", 100)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.categoryId
}