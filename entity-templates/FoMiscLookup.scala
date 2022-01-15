package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FoMiscLookup extends StorableClass(FoMiscLookup) {
	object values extends ValuesObject {
		val itemId = new IntFieldValue(self, FoMiscLookup.fields.itemId)
		val itemName = new NullableStringFieldValue(self, FoMiscLookup.fields.itemName)
		val active = new NullableBooleanFIeldValue(self, FoMiscLookup.fields.active)
		val displayOrder = new NullableDoubleFieldValue(self, FoMiscLookup.fields.displayOrder)
		val regCodeRowId = new NullableIntFieldValue(self, FoMiscLookup.fields.regCodeRowId)
		val createdOn = new NullableLocalDateTimeFieldValue(self, FoMiscLookup.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, FoMiscLookup.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, FoMiscLookup.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, FoMiscLookup.fields.updatedBy)
		val taxable = new NullableBooleanFIeldValue(self, FoMiscLookup.fields.taxable)
	}
}

object FoMiscLookup extends StorableObject[FoMiscLookup] {
	val entityName: String = "FO_MISC_LOOKUP"

	object fields extends FieldsObject {
		val itemId = new IntDatabaseField(self, "ITEM_ID")
		val itemName = new NullableStringDatabaseField(self, "ITEM_NAME", 200)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
		val regCodeRowId = new NullableIntDatabaseField(self, "REG_CODE_ROW_ID")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val taxable = new NullableBooleanDatabaseField(self, "TAXABLE")
	}

	def primaryKey: IntDatabaseField = fields.itemId
}