package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PersonsTag extends StorableClass(PersonsTag) {
	object values extends ValuesObject {
		val assignId = new IntFieldValue(self, PersonsTag.fields.assignId)
		val personId = new NullableIntFieldValue(self, PersonsTag.fields.personId)
		val tagId = new NullableIntFieldValue(self, PersonsTag.fields.tagId)
		val createdOn = new NullableLocalDateTimeFieldValue(self, PersonsTag.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, PersonsTag.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, PersonsTag.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, PersonsTag.fields.updatedBy)
	}
}

object PersonsTag extends StorableObject[PersonsTag] {
	val entityName: String = "PERSONS_TAGS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val personId = new NullableIntDatabaseField(self, "PERSON_ID")
		val tagId = new NullableIntDatabaseField(self, "TAG_ID")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.assignId
}