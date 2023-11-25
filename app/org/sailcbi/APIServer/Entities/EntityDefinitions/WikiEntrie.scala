package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class WikiEntrie extends StorableClass(WikiEntrie) {
	override object values extends ValuesObject {
		val entryId = new IntFieldValue(self, WikiEntrie.fields.entryId)
		val app = new IntFieldValue(self, WikiEntrie.fields.app)
		val page = new IntFieldValue(self, WikiEntrie.fields.page)
		val content = new NullableStringFieldValue(self, WikiEntrie.fields.content)
		val createdOn = new DateTimeFieldValue(self, WikiEntrie.fields.createdOn)
		val createdBy = new StringFieldValue(self, WikiEntrie.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, WikiEntrie.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, WikiEntrie.fields.updatedBy)
	}
}

object WikiEntrie extends StorableObject[WikiEntrie] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "WIKI_ENTRIES"

	object fields extends FieldsObject {
		val entryId = new IntDatabaseField(self, "ENTRY_ID")
		@NullableInDatabase
		val app = new IntDatabaseField(self, "APP")
		@NullableInDatabase
		val page = new IntDatabaseField(self, "PAGE")
		val content = new NullableStringDatabaseField(self, "CONTENT", -1)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.entryId
}