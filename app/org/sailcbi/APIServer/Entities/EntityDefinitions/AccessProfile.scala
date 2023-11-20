package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class AccessProfile extends StorableClass(AccessProfile) {
	override object values extends ValuesObject {
		val accessProfileId = new IntFieldValue(self, AccessProfile.fields.accessProfileId)
		val name = new StringFieldValue(self, AccessProfile.fields.name)
		val description = new NullableStringFieldValue(self, AccessProfile.fields.description)
		val displayOrder = new DoubleFieldValue(self, AccessProfile.fields.displayOrder)
	}
}

object AccessProfile extends StorableObject[AccessProfile] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "ACCESS_PROFILES"

	object fields extends FieldsObject {
		val accessProfileId = new IntDatabaseField(self, "ACCESS_PROFILE_ID")
		val name = new StringDatabaseField(self, "NAME", 50)
		val description = new NullableStringDatabaseField(self, "DESCRIPTION", 100)
		val displayOrder = new DoubleDatabaseField(self, "DISPLAY_ORDER")
	}

	def primaryKey: IntDatabaseField = fields.accessProfileId
}