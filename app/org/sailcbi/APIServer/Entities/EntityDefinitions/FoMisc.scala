package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FoMisc extends StorableClass(FoMisc) {
	override object values extends ValuesObject {
		val rowId = new IntFieldValue(self, FoMisc.fields.rowId)
		val itemId = new IntFieldValue(self, FoMisc.fields.itemId)
		val closeId = new IntFieldValue(self, FoMisc.fields.closeId)
		val qtySold = new DoubleFieldValue(self, FoMisc.fields.qtySold)
		val totalPrice = new DoubleFieldValue(self, FoMisc.fields.totalPrice)
		val createdOn = new DateTimeFieldValue(self, FoMisc.fields.createdOn)
		val createdBy = new StringFieldValue(self, FoMisc.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, FoMisc.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, FoMisc.fields.updatedBy)
		val qtyComped = new DoubleFieldValue(self, FoMisc.fields.qtyComped)
		val totalComp = new DoubleFieldValue(self, FoMisc.fields.totalComp)
		val taxRate = new NullableDoubleFieldValue(self, FoMisc.fields.taxRate)
		val totalPretax = new NullableDoubleFieldValue(self, FoMisc.fields.totalPretax)
	}
}

object FoMisc extends StorableObject[FoMisc] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "FO_MISC"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val itemId = new IntDatabaseField(self, "ITEM_ID")
		val closeId = new IntDatabaseField(self, "CLOSE_ID")
		val qtySold = new DoubleDatabaseField(self, "QTY_SOLD")
		val totalPrice = new DoubleDatabaseField(self, "TOTAL_PRICE")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val qtyComped = new DoubleDatabaseField(self, "QTY_COMPED")
		val totalComp = new DoubleDatabaseField(self, "TOTAL_COMP")
		val taxRate = new NullableDoubleDatabaseField(self, "TAX_RATE")
		val totalPretax = new NullableDoubleDatabaseField(self, "TOTAL_PRETAX")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}