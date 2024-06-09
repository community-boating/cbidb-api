package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class TextFile extends StorableClass(TextFile) {
	override object values extends ValuesObject {
		val fileId = new IntFieldValue(self, TextFile.fields.fileId)
		val fileName = new NullableStringFieldValue(self, TextFile.fields.fileName)
		val mimeType = new NullableStringFieldValue(self, TextFile.fields.mimeType)
		val clobContent = new NullableStringFieldValue(self, TextFile.fields.clobContent)
		val createdOn = new DateTimeFieldValue(self, TextFile.fields.createdOn)
		val createdBy = new StringFieldValue(self, TextFile.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, TextFile.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, TextFile.fields.updatedBy)
	}
}

object TextFile extends StorableObject[TextFile] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "TEXT_FILES"

	object fields extends FieldsObject {
		val fileId = new IntDatabaseField(self, "FILE_ID")
		val fileName = new NullableStringDatabaseField(self, "FILE_NAME", 100)
		val mimeType = new NullableStringDatabaseField(self, "MIME_TYPE", 100)
		val clobContent = new NullableStringDatabaseField(self, "CLOB_CONTENT", -1)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.fileId
}