package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FoMisc extends StorableClass(FoMisc) {
	object values extends ValuesObject {
		val rowId = new IntFieldValue(self, FoMisc.fields.rowId)
		val itemId = new NullableIntFieldValue(self, FoMisc.fields.itemId)
		val closeId = new NullableIntFieldValue(self, FoMisc.fields.closeId)
		val qtySold = new NullableDoubleFieldValue(self, FoMisc.fields.qtySold)
		val totalPrice = new NullableDoubleFieldValue(self, FoMisc.fields.totalPrice)
		val createdOn = new NullableLocalDateTimeFieldValue(self, FoMisc.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, FoMisc.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, FoMisc.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, FoMisc.fields.updatedBy)
		val qtyComped = new NullableDoubleFieldValue(self, FoMisc.fields.qtyComped)
		val totalComp = new NullableDoubleFieldValue(self, FoMisc.fields.totalComp)
		val taxRate = new NullableDoubleFieldValue(self, FoMisc.fields.taxRate)
		val totalPretax = new NullableDoubleFieldValue(self, FoMisc.fields.totalPretax)
	}
}

object FoMisc extends StorableObject[FoMisc] {
	val entityName: String = "FO_MISC"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val itemId = new NullableIntDatabaseField(self, "ITEM_ID")
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val qtySold = new NullableDoubleDatabaseField(self, "QTY_SOLD")
		val totalPrice = new NullableDoubleDatabaseField(self, "TOTAL_PRICE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val qtyComped = new NullableDoubleDatabaseField(self, "QTY_COMPED")
		val totalComp = new NullableDoubleDatabaseField(self, "TOTAL_COMP")
		val taxRate = new NullableDoubleDatabaseField(self, "TAX_RATE")
		val totalPretax = new NullableDoubleDatabaseField(self, "TOTAL_PRETAX")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}