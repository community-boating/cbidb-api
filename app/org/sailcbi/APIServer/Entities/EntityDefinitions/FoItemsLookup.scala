package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FoItemsLookup extends StorableClass(FoItemsLookup) {
	override object values extends ValuesObject {
		val itemId = new IntFieldValue(self, FoItemsLookup.fields.itemId)
		val categoryId = new IntFieldValue(self, FoItemsLookup.fields.categoryId)
		val itemName = new StringFieldValue(self, FoItemsLookup.fields.itemName)
		val createdOn = new NullableDateTimeFieldValue(self, FoItemsLookup.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, FoItemsLookup.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, FoItemsLookup.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, FoItemsLookup.fields.updatedBy)
		val active = new NullableBooleanFieldValue(self, FoItemsLookup.fields.active)
		val displayOrder = new DoubleFieldValue(self, FoItemsLookup.fields.displayOrder)
		val currentPrice = new DoubleFieldValue(self, FoItemsLookup.fields.currentPrice)
		val regCodeRowId = new NullableIntFieldValue(self, FoItemsLookup.fields.regCodeRowId)
		val taxable = new NullableBooleanFieldValue(self, FoItemsLookup.fields.taxable)
	}
}

object FoItemsLookup extends StorableObject[FoItemsLookup] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "FO_ITEMS_LOOKUP"

	object fields extends FieldsObject {
		val itemId = new IntDatabaseField(self, "ITEM_ID")
		val categoryId = new IntDatabaseField(self, "CATEGORY_ID")
		val itemName = new StringDatabaseField(self, "ITEM_NAME", 200)
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val displayOrder = new DoubleDatabaseField(self, "DISPLAY_ORDER")
		val currentPrice = new DoubleDatabaseField(self, "CURRENT_PRICE")
		val regCodeRowId = new NullableIntDatabaseField(self, "REG_CODE_ROW_ID")
		val taxable = new NullableBooleanDatabaseField(self, "TAXABLE")
	}

	def primaryKey: IntDatabaseField = fields.itemId
}