package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class UapComment extends StorableClass(UapComment) {
	object values extends ValuesObject {
		val personId = new IntFieldValue(self, UapComment.fields.personId)
		val uapComment = new NullableUnknownFieldType(self, UapComment.fields.uapComment)
		val createdOn = new NullableLocalDateTimeFieldValue(self, UapComment.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, UapComment.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, UapComment.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, UapComment.fields.updatedBy)
	}
}

object UapComment extends StorableObject[UapComment] {
	val entityName: String = "UAP_COMMENTS"

	object fields extends FieldsObject {
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val uapComment = new NullableUnknownFieldType(self, "UAP_COMMENT")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.personId
}