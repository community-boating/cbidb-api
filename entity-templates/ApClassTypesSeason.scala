package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ApClassTypesSeason extends StorableClass(ApClassTypesSeason) {
	object values extends ValuesObject {
		val assignId = new IntFieldValue(self, ApClassTypesSeason.fields.assignId)
		val typeId = new IntFieldValue(self, ApClassTypesSeason.fields.typeId)
		val season = new DoubleFieldValue(self, ApClassTypesSeason.fields.season)
	}
}

object ApClassTypesSeason extends StorableObject[ApClassTypesSeason] {
	val entityName: String = "AP_CLASS_TYPES_SEASONS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val season = new DoubleDatabaseField(self, "SEASON")
	}

	def primaryKey: IntDatabaseField = fields.assignId
}