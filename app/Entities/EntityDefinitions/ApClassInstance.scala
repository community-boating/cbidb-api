package Entities.EntityDefinitions

import CbiUtil.Initializable
import Storable.Fields.FieldValue.{IntFieldValue, NullableStringFieldValue}
import Storable.Fields.{IntDatabaseField, NullableStringDatabaseField}
import Storable._

class ApClassInstance extends StorableClass {
	this.setCompanion(ApClassInstance)

	object references extends ReferencesObject {
		var apClassFormat = new Initializable[ApClassFormat]
	}

	object values extends ValuesObject {
		val instanceId = new IntFieldValue(self, ApClassInstance.fields.instanceId)
		val formatId = new IntFieldValue(self, ApClassInstance.fields.formatId)
		val locationString = new NullableStringFieldValue(self, ApClassInstance.fields.locationString)
	}

}

object ApClassInstance extends StorableObject[ApClassInstance] {
	val entityName: String = "AP_CLASS_INSTANCES"

	object fields extends FieldsObject {
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val formatId = new IntDatabaseField(self, "FORMAT_ID")
		val locationString = new NullableStringDatabaseField(self, "LOCATION_STRING", 500)
	}

	def primaryKey: IntDatabaseField = fields.instanceId
}