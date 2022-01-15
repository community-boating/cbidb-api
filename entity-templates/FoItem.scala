package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FoItem extends StorableClass(FoItem) {
	object values extends ValuesObject {
		val rowId = new IntFieldValue(self, FoItem.fields.rowId)
		val itemId = new NullableIntFieldValue(self, FoItem.fields.itemId)
		val closeId = new NullableIntFieldValue(self, FoItem.fields.closeId)
		val unitPrice = new NullableDoubleFieldValue(self, FoItem.fields.unitPrice)
		val discountsTotal = new NullableDoubleFieldValue(self, FoItem.fields.discountsTotal)
		val qtySold = new NullableDoubleFieldValue(self, FoItem.fields.qtySold)
		val qtyComped = new NullableDoubleFieldValue(self, FoItem.fields.qtyComped)
		val createdOn = new NullableLocalDateTimeFieldValue(self, FoItem.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, FoItem.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, FoItem.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, FoItem.fields.updatedBy)
		val taxRate = new NullableDoubleFieldValue(self, FoItem.fields.taxRate)
	}
}

object FoItem extends StorableObject[FoItem] {
	val entityName: String = "FO_ITEMS"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val itemId = new NullableIntDatabaseField(self, "ITEM_ID")
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val unitPrice = new NullableDoubleDatabaseField(self, "UNIT_PRICE")
		val discountsTotal = new NullableDoubleDatabaseField(self, "DISCOUNTS_TOTAL")
		val qtySold = new NullableDoubleDatabaseField(self, "QTY_SOLD")
		val qtyComped = new NullableDoubleDatabaseField(self, "QTY_COMPED")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val taxRate = new NullableDoubleDatabaseField(self, "TAX_RATE")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}