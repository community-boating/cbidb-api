package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FoItemType extends StorableClass(FoItemType) {
	object values extends ValuesObject {
		val rowId = new IntFieldValue(self, FoItemType.fields.rowId)
		val typeId = new NullableIntFieldValue(self, FoItemType.fields.typeId)
		val closeId = new NullableIntFieldValue(self, FoItemType.fields.closeId)
		val discountsTotal = new NullableDoubleFieldValue(self, FoItemType.fields.discountsTotal)
		val qtySold = new NullableDoubleFieldValue(self, FoItemType.fields.qtySold)
		val qtyComped = new NullableDoubleFieldValue(self, FoItemType.fields.qtyComped)
		val createdOn = new NullableLocalDateTimeFieldValue(self, FoItemType.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, FoItemType.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, FoItemType.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, FoItemType.fields.updatedBy)
		val unitPrice = new NullableDoubleFieldValue(self, FoItemType.fields.unitPrice)
		val taxRate = new NullableDoubleFieldValue(self, FoItemType.fields.taxRate)
	}
}

object FoItemType extends StorableObject[FoItemType] {
	val entityName: String = "FO_ITEM_TYPES"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val typeId = new NullableIntDatabaseField(self, "TYPE_ID")
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val discountsTotal = new NullableDoubleDatabaseField(self, "DISCOUNTS_TOTAL")
		val qtySold = new NullableDoubleDatabaseField(self, "QTY_SOLD")
		val qtyComped = new NullableDoubleDatabaseField(self, "QTY_COMPED")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val unitPrice = new NullableDoubleDatabaseField(self, "UNIT_PRICE")
		val taxRate = new NullableDoubleDatabaseField(self, "TAX_RATE")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}