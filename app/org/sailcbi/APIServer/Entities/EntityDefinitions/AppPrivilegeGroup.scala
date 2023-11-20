package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class AppPrivilegeGroup extends StorableClass(AppPrivilegeGroup) {
	override object values extends ValuesObject {
		val groupId = new IntFieldValue(self, AppPrivilegeGroup.fields.groupId)
		val groupName = new NullableStringFieldValue(self, AppPrivilegeGroup.fields.groupName)
		val createdOn = new NullableDateTimeFieldValue(self, AppPrivilegeGroup.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, AppPrivilegeGroup.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, AppPrivilegeGroup.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, AppPrivilegeGroup.fields.updatedBy)
	}
}

object AppPrivilegeGroup extends StorableObject[AppPrivilegeGroup] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "APP_PRIVILEGE_GROUPS"

	object fields extends FieldsObject {
		val groupId = new IntDatabaseField(self, "GROUP_ID")
		val groupName = new NullableStringDatabaseField(self, "GROUP_NAME", 500)
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.groupId
}