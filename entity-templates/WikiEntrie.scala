package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class WikiEntrie extends StorableClass(WikiEntrie) {
	object values extends ValuesObject {
		val entryId = new IntFieldValue(self, WikiEntrie.fields.entryId)
		val app = new NullableDoubleFieldValue(self, WikiEntrie.fields.app)
		val page = new NullableDoubleFieldValue(self, WikiEntrie.fields.page)
		val content = new NullableUnknownFieldType(self, WikiEntrie.fields.content)
		val createdOn = new NullableLocalDateTimeFieldValue(self, WikiEntrie.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, WikiEntrie.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, WikiEntrie.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, WikiEntrie.fields.updatedBy)
	}
}

object WikiEntrie extends StorableObject[WikiEntrie] {
	val entityName: String = "WIKI_ENTRIES"

	object fields extends FieldsObject {
		val entryId = new IntDatabaseField(self, "ENTRY_ID")
		val app = new NullableDoubleDatabaseField(self, "APP")
		val page = new NullableDoubleDatabaseField(self, "PAGE")
		val content = new NullableUnknownFieldType(self, "CONTENT")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.entryId
}