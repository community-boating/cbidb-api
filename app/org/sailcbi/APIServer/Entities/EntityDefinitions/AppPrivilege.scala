package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class AppPrivilege extends StorableClass(AppPrivilege) {
	override object values extends ValuesObject {
		val privilegeId = new IntFieldValue(self, AppPrivilege.fields.privilegeId)
		val privilegeName = new NullableStringFieldValue(self, AppPrivilege.fields.privilegeName)
		val createdOn = new NullableDateTimeFieldValue(self, AppPrivilege.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, AppPrivilege.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, AppPrivilege.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, AppPrivilege.fields.updatedBy)
		val groupId = new NullableIntFieldValue(self, AppPrivilege.fields.groupId)
	}
}

object AppPrivilege extends StorableObject[AppPrivilege] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "APP_PRIVILEGES"

	object fields extends FieldsObject {
		val privilegeId = new IntDatabaseField(self, "PRIVILEGE_ID")
		val privilegeName = new NullableStringDatabaseField(self, "PRIVILEGE_NAME", 100)
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val groupId = new NullableIntDatabaseField(self, "GROUP_ID")
	}

	def primaryKey: IntDatabaseField = fields.privilegeId
}