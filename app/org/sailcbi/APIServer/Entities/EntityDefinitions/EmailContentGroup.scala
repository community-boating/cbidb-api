package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class EmailContentGroup extends StorableClass(EmailContentGroup) {
	override object values extends ValuesObject {
		val groupId = new IntFieldValue(self, EmailContentGroup.fields.groupId)
		val groupName = new StringFieldValue(self, EmailContentGroup.fields.groupName)
		val createdOn = new DateTimeFieldValue(self, EmailContentGroup.fields.createdOn)
		val createdBy = new StringFieldValue(self, EmailContentGroup.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, EmailContentGroup.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, EmailContentGroup.fields.updatedBy)
	}
}

object EmailContentGroup extends StorableObject[EmailContentGroup] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "EMAIL_CONTENT_GROUPS"

	object fields extends FieldsObject {
		val groupId = new IntDatabaseField(self, "GROUP_ID")
		@NullableInDatabase
		val groupName = new StringDatabaseField(self, "GROUP_NAME", 50)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.groupId
}