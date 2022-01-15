package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpGroup extends StorableClass(JpGroup) {
	object values extends ValuesObject {
		val groupId = new IntFieldValue(self, JpGroup.fields.groupId)
		val groupName = new StringFieldValue(self, JpGroup.fields.groupName)
		val createdOn = new NullableLocalDateTimeFieldValue(self, JpGroup.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpGroup.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, JpGroup.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpGroup.fields.updatedBy)
		val active = new NullableBooleanFIeldValue(self, JpGroup.fields.active)
	}
}

object JpGroup extends StorableObject[JpGroup] {
	val entityName: String = "JP_GROUPS"

	object fields extends FieldsObject {
		val groupId = new IntDatabaseField(self, "GROUP_ID")
		@NullableInDatabase
		val groupName = new StringDatabaseField(self, "GROUP_NAME", 200)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
	}

	def primaryKey: IntDatabaseField = fields.groupId
}