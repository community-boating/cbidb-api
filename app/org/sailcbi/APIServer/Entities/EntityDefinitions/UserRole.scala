package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues.IntFieldValue
import com.coleji.neptune.Storable.Fields.IntDatabaseField
import com.coleji.neptune.Storable.{FieldsObject, StorableClass, StorableObject, ValuesObject}

class UserRole extends StorableClass(UserRole) {
	object values extends ValuesObject {
		val assignId = new IntFieldValue(self, UserRole.fields.assignId)
		val userId = new IntFieldValue(self, UserRole.fields.userId)
		val roleId = new IntFieldValue(self, UserRole.fields.roleId)
	}
}

object UserRole extends StorableObject[UserRole] {
	val entityName: String = "USERS_ROLES"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val userId = new IntDatabaseField(self, "USER_ID")
		val roleId = new IntDatabaseField(self, "ROLE_ID")
	}

	def primaryKey: IntDatabaseField = fields.assignId
}
