package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class TextFile extends StorableClass(TextFile) {
	object values extends ValuesObject {
		val fileId = new IntFieldValue(self, TextFile.fields.fileId)
		val fileName = new NullableStringFieldValue(self, TextFile.fields.fileName)
		val mimeType = new NullableStringFieldValue(self, TextFile.fields.mimeType)
		val clobContent = new NullableUnknownFieldType(self, TextFile.fields.clobContent)
		val createdOn = new NullableLocalDateTimeFieldValue(self, TextFile.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, TextFile.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, TextFile.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, TextFile.fields.updatedBy)
	}
}

object TextFile extends StorableObject[TextFile] {
	val entityName: String = "TEXT_FILES"

	object fields extends FieldsObject {
		val fileId = new IntDatabaseField(self, "FILE_ID")
		val fileName = new NullableStringDatabaseField(self, "FILE_NAME", 100)
		val mimeType = new NullableStringDatabaseField(self, "MIME_TYPE", 100)
		val clobContent = new NullableUnknownFieldType(self, "CLOB_CONTENT")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.fileId
}