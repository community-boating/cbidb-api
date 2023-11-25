package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class AccessProfileRelationship extends StorableClass(AccessProfileRelationship) {
	override object values extends ValuesObject {
		val rowId = new IntFieldValue(self, AccessProfileRelationship.fields.rowId)
		val managingProfileId = new IntFieldValue(self, AccessProfileRelationship.fields.managingProfileId)
		val subordinateProfileId = new IntFieldValue(self, AccessProfileRelationship.fields.subordinateProfileId)
	}
}

object AccessProfileRelationship extends StorableObject[AccessProfileRelationship] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "ACCESS_PROFILES_RELATIONSHIPS"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val managingProfileId = new IntDatabaseField(self, "MANAGING_PROFILE_ID")
		val subordinateProfileId = new IntDatabaseField(self, "SUBORDINATE_PROFILE_ID")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}