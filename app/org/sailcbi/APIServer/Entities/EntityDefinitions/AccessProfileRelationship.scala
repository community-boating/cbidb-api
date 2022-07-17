package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues.IntFieldValue
import com.coleji.neptune.Storable.Fields.IntDatabaseField
import com.coleji.neptune.Storable.{FieldsObject, StorableClass, StorableObject, ValuesObject}

class AccessProfileRelationship extends StorableClass(AccessProfileRelationship) {
	object values extends ValuesObject {
		val rowId = new IntFieldValue(self, AccessProfileRelationship.fields.rowId)
		val managingProfileId = new IntFieldValue(self, AccessProfileRelationship.fields.managingProfileId)
		val subordinateProfileId = new IntFieldValue(self, AccessProfileRelationship.fields.subordinateProfileId)
	}
}

object AccessProfileRelationship extends StorableObject[AccessProfileRelationship] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	val entityName: String = "ACCESS_PROFILES_RELATIONSHIPS"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val managingProfileId = new IntDatabaseField(self, "MANAGING_PROFILE_ID")
		val subordinateProfileId = new IntDatabaseField(self, "SUBORDINATE_PROFILE_ID")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}
