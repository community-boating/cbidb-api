package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class UapComment extends StorableClass(UapComment) {
	override object values extends ValuesObject {
		val personId = new IntFieldValue(self, UapComment.fields.personId)
		val uapComment = new StringFieldValue(self, UapComment.fields.uapComment)
		val createdOn = new DateTimeFieldValue(self, UapComment.fields.createdOn)
		val createdBy = new StringFieldValue(self, UapComment.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, UapComment.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, UapComment.fields.updatedBy)
	}
}

object UapComment extends StorableObject[UapComment] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "UAP_COMMENTS"

	object fields extends FieldsObject {
		val personId = new IntDatabaseField(self, "PERSON_ID")
		@NullableInDatabase
		val uapComment = new StringDatabaseField(self, "UAP_COMMENT", -1)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.personId
}