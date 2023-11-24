package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class PersonTypesField extends StorableClass(PersonTypesField) {
	override object values extends ValuesObject {
		val assignId = new IntFieldValue(self, PersonTypesField.fields.assignId)
		val typeId = new IntFieldValue(self, PersonTypesField.fields.typeId)
		val itemName = new StringFieldValue(self, PersonTypesField.fields.itemName)
		val createdOn = new DateTimeFieldValue(self, PersonTypesField.fields.createdOn)
		val createdBy = new StringFieldValue(self, PersonTypesField.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, PersonTypesField.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, PersonTypesField.fields.updatedBy)
		val display = new NullableBooleanFieldValue(self, PersonTypesField.fields.display)
	}
}

object PersonTypesField extends StorableObject[PersonTypesField] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "PERSON_TYPES_FIELDS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		@NullableInDatabase
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		@NullableInDatabase
		val itemName = new StringDatabaseField(self, "ITEM_NAME", 100)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val display = new NullableBooleanDatabaseField(self, "DISPLAY")
	}

	def primaryKey: IntDatabaseField = fields.assignId
}