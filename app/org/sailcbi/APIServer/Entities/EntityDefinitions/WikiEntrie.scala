package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class WikiEntrie extends StorableClass(WikiEntrie) {
	override object values extends ValuesObject {
		val entryId = new IntFieldValue(self, WikiEntrie.fields.entryId)
		val app = new DoubleFieldValue(self, WikiEntrie.fields.app)
		val page = new DoubleFieldValue(self, WikiEntrie.fields.page)
		val content = new NullableUnknownFieldType(self, WikiEntrie.fields.content)
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
		val app = new DoubleDatabaseField(self, "APP")
		val page = new DoubleDatabaseField(self, "PAGE")
		val content = new NullableUnknownFieldType(self, "CONTENT")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.entryId
}