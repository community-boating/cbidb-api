package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FoItemsLookup extends StorableClass(FoItemsLookup) {
	object values extends ValuesObject {
		val itemId = new IntFieldValue(self, FoItemsLookup.fields.itemId)
		val categoryId = new NullableIntFieldValue(self, FoItemsLookup.fields.categoryId)
		val itemName = new NullableStringFieldValue(self, FoItemsLookup.fields.itemName)
		val createdOn = new NullableLocalDateTimeFieldValue(self, FoItemsLookup.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, FoItemsLookup.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, FoItemsLookup.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, FoItemsLookup.fields.updatedBy)
		val active = new NullableBooleanFIeldValue(self, FoItemsLookup.fields.active)
		val displayOrder = new NullableDoubleFieldValue(self, FoItemsLookup.fields.displayOrder)
		val currentPrice = new DoubleFieldValue(self, FoItemsLookup.fields.currentPrice)
		val regCodeRowId = new NullableIntFieldValue(self, FoItemsLookup.fields.regCodeRowId)
		val taxable = new NullableBooleanFIeldValue(self, FoItemsLookup.fields.taxable)
	}
}

object FoItemsLookup extends StorableObject[FoItemsLookup] {
	val entityName: String = "FO_ITEMS_LOOKUP"

	object fields extends FieldsObject {
		val itemId = new IntDatabaseField(self, "ITEM_ID")
		val categoryId = new NullableIntDatabaseField(self, "CATEGORY_ID")
		val itemName = new NullableStringDatabaseField(self, "ITEM_NAME", 200)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
		val currentPrice = new DoubleDatabaseField(self, "CURRENT_PRICE")
		val regCodeRowId = new NullableIntDatabaseField(self, "REG_CODE_ROW_ID")
		val taxable = new NullableBooleanDatabaseField(self, "TAXABLE")
	}

	def primaryKey: IntDatabaseField = fields.itemId
}