package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues.{IntFieldValue, NullableStringFieldValue, StringFieldValue}
import com.coleji.neptune.Storable.Fields.{IntDatabaseField, NullableStringDatabaseField, StringDatabaseField}
import com.coleji.neptune.Storable.{FieldsObject, StorableClass, StorableObject, ValuesObject}

class AccessProfile extends StorableClass(AccessProfile) {
	object values extends ValuesObject {
		val accessProfileId = new IntFieldValue(self, AccessProfile.fields.accessProfileId)
		val name = new StringFieldValue(self, AccessProfile.fields.name)
		val description = new NullableStringFieldValue(self, AccessProfile.fields.description)
		val displayOrder = new IntFieldValue(self, AccessProfile.fields.displayOrder)
	}
}

object AccessProfile extends StorableObject[AccessProfile] {
	override val useRuntimeFieldnamesForJson: Boolean = true
	
	val entityName: String = "ACCESS_PROFILES"

	object fields extends FieldsObject {
		val accessProfileId = new IntDatabaseField(self, "ACCESS_PROFILE_ID")
		val name = new StringDatabaseField(self, "NAME", 50)
		val description = new NullableStringDatabaseField(self, "DESCRIPTION", 100)
		val displayOrder = new IntDatabaseField(self, "DISPLAY_ORDER")
	}

	def primaryKey: IntDatabaseField = fields.accessProfileId
}
