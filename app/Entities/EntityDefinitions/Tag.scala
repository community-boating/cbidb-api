package Entities.EntityDefinitions

import Storable.Fields.FieldValue.{IntFieldValue, StringFieldValue}
import Storable.Fields.{IntDatabaseField, StringDatabaseField}
import Storable._

class Tag extends StorableClass {
	this.setCompanion(Tag)

	object references extends ReferencesObject {}

	object values extends ValuesObject {
		val tagId = new IntFieldValue(self, Tag.fields.tagId)
		val tagName = new StringFieldValue(self, Tag.fields.tagName)
	}

}

object Tag extends StorableObject[Tag] {
	val entityName: String = "TAGS"

	object fields extends FieldsObject {
		val tagId = new IntDatabaseField(self, "TAG_ID")
		val tagName = new StringDatabaseField(self, "TAG_NAME", 100)
	}

	def primaryKey: IntDatabaseField = fields.tagId

	object specialIDs {
		val TAG_ID_CORPORATION: Int = 5
	}

}