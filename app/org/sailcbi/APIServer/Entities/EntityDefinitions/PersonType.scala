package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PersonType extends StorableClass(PersonType) {
	override object values extends ValuesObject {
		val typeId = new IntFieldValue(self, PersonType.fields.typeId)
		val typeName = new StringFieldValue(self, PersonType.fields.typeName)
		val createdOn = new DateTimeFieldValue(self, PersonType.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, PersonType.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, PersonType.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, PersonType.fields.updatedBy)
		val active = new NullableBooleanFieldValue(self, PersonType.fields.active)
		val displayOrder = new DoubleFieldValue(self, PersonType.fields.displayOrder)
	}
}

object PersonType extends StorableObject[PersonType] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "PERSON_TYPES"

	object fields extends FieldsObject {
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val typeName = new StringDatabaseField(self, "TYPE_NAME", 100)
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val displayOrder = new DoubleDatabaseField(self, "DISPLAY_ORDER")
	}

	def primaryKey: IntDatabaseField = fields.typeId
}