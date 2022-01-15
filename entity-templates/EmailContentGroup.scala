package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class EmailContentGroup extends StorableClass(EmailContentGroup) {
	object values extends ValuesObject {
		val groupId = new IntFieldValue(self, EmailContentGroup.fields.groupId)
		val groupName = new NullableStringFieldValue(self, EmailContentGroup.fields.groupName)
		val createdOn = new NullableLocalDateTimeFieldValue(self, EmailContentGroup.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, EmailContentGroup.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, EmailContentGroup.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, EmailContentGroup.fields.updatedBy)
	}
}

object EmailContentGroup extends StorableObject[EmailContentGroup] {
	val entityName: String = "EMAIL_CONTENT_GROUPS"

	object fields extends FieldsObject {
		val groupId = new IntDatabaseField(self, "GROUP_ID")
		val groupName = new NullableStringDatabaseField(self, "GROUP_NAME", 50)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.groupId
}