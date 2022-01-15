package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PersonTypesField extends StorableClass(PersonTypesField) {
	object values extends ValuesObject {
		val assignId = new IntFieldValue(self, PersonTypesField.fields.assignId)
		val typeId = new NullableIntFieldValue(self, PersonTypesField.fields.typeId)
		val itemName = new NullableStringFieldValue(self, PersonTypesField.fields.itemName)
		val createdOn = new NullableLocalDateTimeFieldValue(self, PersonTypesField.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, PersonTypesField.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, PersonTypesField.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, PersonTypesField.fields.updatedBy)
		val display = new NullableBooleanFIeldValue(self, PersonTypesField.fields.display)
	}
}

object PersonTypesField extends StorableObject[PersonTypesField] {
	val entityName: String = "PERSON_TYPES_FIELDS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val typeId = new NullableIntDatabaseField(self, "TYPE_ID")
		val itemName = new NullableStringDatabaseField(self, "ITEM_NAME", 100)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val display = new NullableBooleanDatabaseField(self, "DISPLAY")
	}

	def primaryKey: IntDatabaseField = fields.assignId
}