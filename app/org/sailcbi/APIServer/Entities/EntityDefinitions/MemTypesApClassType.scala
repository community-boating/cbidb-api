package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._

class MemTypesApClassType extends StorableClass(MemTypesApClassType) {
	override object values extends ValuesObject {
		val membershipTypeId = new IntFieldValue(self, MemTypesApClassType.fields.membershipTypeId)
		val apClassTypeId = new IntFieldValue(self, MemTypesApClassType.fields.apClassTypeId)
	}
}

object MemTypesApClassType extends StorableObject[MemTypesApClassType] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "MEM_TYPES_AP_CLASS_TYPES"

	object fields extends FieldsObject {
		val membershipTypeId = new IntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
		val apClassTypeId = new IntDatabaseField(self, "AP_CLASS_TYPE_ID")
	}

	def primaryKey: IntDatabaseField = fields.membershipTypeId
}