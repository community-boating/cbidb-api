package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.framework.Storable.FieldValues.IntFieldValue
import com.coleji.framework.Storable.Fields.IntDatabaseField
import com.coleji.framework.Storable._
import com.coleji.framework.Util.Initializable

class PersonTag extends StorableClass(PersonTag) {
	override object references extends ReferencesObject {
		var person = new Initializable[Person]
		var tag = new Initializable[Tag]
	}

	object values extends ValuesObject {
		val assignId = new IntFieldValue(self, PersonTag.fields.assignId)
		val personId = new IntFieldValue(self, PersonTag.fields.personId)
		val tagId = new IntFieldValue(self, PersonTag.fields.tagId)
	}
}

object PersonTag extends StorableObject[PersonTag] {
	val entityName: String = "PERSONS_TAGS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val tagId = new IntDatabaseField(self, "TAG_ID")
	}

	def primaryKey: IntDatabaseField = fields.assignId
}