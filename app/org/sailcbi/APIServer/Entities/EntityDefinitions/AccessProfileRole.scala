package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues.IntFieldValue
import com.coleji.neptune.Storable.Fields.IntDatabaseField
import com.coleji.neptune.Storable.{FieldsObject, StorableClass, StorableObject, ValuesObject}

class AccessProfileRole extends StorableClass(AccessProfileRole) {
	object values extends ValuesObject {
		val assignId = new IntFieldValue(self, AccessProfileRole.fields.assignId)
		val accessProfileId = new IntFieldValue(self, AccessProfileRole.fields.accessProfileId)
		val roleId = new IntFieldValue(self, AccessProfileRole.fields.roleId)
	}
}

object AccessProfileRole extends StorableObject[AccessProfileRole] {
	val entityName: String = "ACCESS_PROFILES_ROLES"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val accessProfileId = new IntDatabaseField(self, "ACCESS_PROFILE_ID")
		val roleId = new IntDatabaseField(self, "ROLE_ID")
	}

	def primaryKey: IntDatabaseField = fields.assignId
}
