package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues.{IntFieldValue, StringFieldValue}
import com.coleji.neptune.Storable.Fields.{IntDatabaseField, StringDatabaseField}
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class Tag extends StorableClass(Tag) {
	object values extends ValuesObject {
		val tagId = new IntFieldValue(self, Tag.fields.tagId)
		val tagName = new StringFieldValue(self, Tag.fields.tagName)
	}
}

object Tag extends StorableObject[Tag] {
	val entityName: String = "TAGS"

	object fields extends FieldsObject {
		val tagId = new IntDatabaseField(self, "TAG_ID")
		@NullableInDatabase
		val tagName = new StringDatabaseField(self, "TAG_NAME", 100)
	}

	def primaryKey: IntDatabaseField = fields.tagId

	object specialIDs {
		val TAG_ID_CORPORATION: Int = 5
	}

}