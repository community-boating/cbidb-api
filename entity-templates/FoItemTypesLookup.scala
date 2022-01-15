package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FoItemTypesLookup extends StorableClass(FoItemTypesLookup) {
	object values extends ValuesObject {
		val typeId = new IntFieldValue(self, FoItemTypesLookup.fields.typeId)
		val itemId = new NullableIntFieldValue(self, FoItemTypesLookup.fields.itemId)
		val description = new NullableStringFieldValue(self, FoItemTypesLookup.fields.description)
		val createdOn = new NullableLocalDateTimeFieldValue(self, FoItemTypesLookup.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, FoItemTypesLookup.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, FoItemTypesLookup.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, FoItemTypesLookup.fields.updatedBy)
		val active = new NullableBooleanFIeldValue(self, FoItemTypesLookup.fields.active)
		val displayOrder = new NullableDoubleFieldValue(self, FoItemTypesLookup.fields.displayOrder)
	}
}

object FoItemTypesLookup extends StorableObject[FoItemTypesLookup] {
	val entityName: String = "FO_ITEM_TYPES_LOOKUP"

	object fields extends FieldsObject {
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val itemId = new NullableIntDatabaseField(self, "ITEM_ID")
		val description = new NullableStringDatabaseField(self, "DESCRIPTION", 100)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
	}

	def primaryKey: IntDatabaseField = fields.typeId
}