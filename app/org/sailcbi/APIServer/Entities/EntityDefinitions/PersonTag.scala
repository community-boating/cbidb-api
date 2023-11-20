package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

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
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val tagId = new IntDatabaseField(self, "TAG_ID")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.assignId
}