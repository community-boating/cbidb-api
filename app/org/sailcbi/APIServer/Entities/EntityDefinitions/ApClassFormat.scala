package org.sailcbi.APIServer.Entities.EntityDefinitions

import org.sailcbi.APIServer.CbiUtil.Initializable
import org.sailcbi.APIServer.Storable.FieldValues.{IntFieldValue, NullableStringFieldValue}
import org.sailcbi.APIServer.Storable.Fields.{IntDatabaseField, NullableStringDatabaseField}
import org.sailcbi.APIServer.Storable._

class ApClassFormat extends StorableClass {
	this.setCompanion(ApClassFormat)

	object references extends ReferencesObject {
		var apClassType = new Initializable[ApClassType]
	}

	object values extends ValuesObject {
		val formatId = new IntFieldValue(self, ApClassFormat.fields.formatId)
		val typeId = new IntFieldValue(self, ApClassFormat.fields.typeId)
		val description = new NullableStringFieldValue(self, ApClassFormat.fields.description)
	}

	override val valuesList = List(
		values.formatId,
		values.typeId,
		values.description
	)
}

object ApClassFormat extends StorableObject[ApClassFormat] {
	val entityName: String = "AP_CLASS_FORMATS"

	object fields extends FieldsObject {
		val formatId = new IntDatabaseField(self, "FORMAT_ID")
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val description = new NullableStringDatabaseField(self, "DESCRIPTION", 100)
	}

	def primaryKey: IntDatabaseField = fields.formatId
}