package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FoItem extends StorableClass(FoItem) {
	override object values extends ValuesObject {
		val rowId = new IntFieldValue(self, FoItem.fields.rowId)
		val itemId = new IntFieldValue(self, FoItem.fields.itemId)
		val closeId = new IntFieldValue(self, FoItem.fields.closeId)
		val unitPrice = new DoubleFieldValue(self, FoItem.fields.unitPrice)
		val discountsTotal = new DoubleFieldValue(self, FoItem.fields.discountsTotal)
		val qtySold = new DoubleFieldValue(self, FoItem.fields.qtySold)
		val qtyComped = new DoubleFieldValue(self, FoItem.fields.qtyComped)
		val createdOn = new DateTimeFieldValue(self, FoItem.fields.createdOn)
		val createdBy = new StringFieldValue(self, FoItem.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, FoItem.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, FoItem.fields.updatedBy)
		val taxRate = new NullableDoubleFieldValue(self, FoItem.fields.taxRate)
	}
}

object FoItem extends StorableObject[FoItem] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "FO_ITEMS"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val itemId = new IntDatabaseField(self, "ITEM_ID")
		val closeId = new IntDatabaseField(self, "CLOSE_ID")
		val unitPrice = new DoubleDatabaseField(self, "UNIT_PRICE")
		val discountsTotal = new DoubleDatabaseField(self, "DISCOUNTS_TOTAL")
		val qtySold = new DoubleDatabaseField(self, "QTY_SOLD")
		val qtyComped = new DoubleDatabaseField(self, "QTY_COMPED")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val taxRate = new NullableDoubleDatabaseField(self, "TAX_RATE")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}