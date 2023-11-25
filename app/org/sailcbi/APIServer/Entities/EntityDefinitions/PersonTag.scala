package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class PersonTag extends StorableClass(PersonTag) {
	override object references extends ReferencesObject {
		val person = new Initializable[Person]
		val tag = new Initializable[Tag]
	}

	override object values extends ValuesObject {
		val assignId = new IntFieldValue(self, PersonTag.fields.assignId)
		val personId = new IntFieldValue(self, PersonTag.fields.personId)
		val tagId = new IntFieldValue(self, PersonTag.fields.tagId)
		val createdOn = new DateTimeFieldValue(self, PersonTag.fields.createdOn)
		val createdBy = new StringFieldValue(self, PersonTag.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, PersonTag.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, PersonTag.fields.updatedBy)
	}
}

object PersonTag extends StorableObject[PersonTag] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "PERSONS_TAGS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		@NullableInDatabase
		val personId = new IntDatabaseField(self, "PERSON_ID")
		@NullableInDatabase
		val tagId = new IntDatabaseField(self, "TAG_ID")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.assignId
}