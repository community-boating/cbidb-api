package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._

class AccessProfileRole extends StorableClass(AccessProfileRole) {
	override object values extends ValuesObject {
		val assignId = new IntFieldValue(self, AccessProfileRole.fields.assignId)
		val accessProfileId = new IntFieldValue(self, AccessProfileRole.fields.accessProfileId)
		val roleId = new IntFieldValue(self, AccessProfileRole.fields.roleId)
		val createdOn = new NullableDateTimeFieldValue(self, AccessProfileRole.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, AccessProfileRole.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, AccessProfileRole.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, AccessProfileRole.fields.updatedBy)
	}
}

object AccessProfileRole extends StorableObject[AccessProfileRole] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "ACCESS_PROFILES_ROLES"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val accessProfileId = new IntDatabaseField(self, "ACCESS_PROFILE_ID")
		val roleId = new IntDatabaseField(self, "ROLE_ID")
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.assignId
}