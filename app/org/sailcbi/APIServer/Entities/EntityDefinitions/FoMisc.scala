package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class FoMisc extends StorableClass(FoMisc) {
	override object values extends ValuesObject {
		val rowId = new IntFieldValue(self, FoMisc.fields.rowId)
		val itemId = new IntFieldValue(self, FoMisc.fields.itemId)
		val closeId = new IntFieldValue(self, FoMisc.fields.closeId)
		val qtySold = new IntFieldValue(self, FoMisc.fields.qtySold)
		val totalPrice = new DoubleFieldValue(self, FoMisc.fields.totalPrice)
		val createdOn = new DateTimeFieldValue(self, FoMisc.fields.createdOn)
		val createdBy = new StringFieldValue(self, FoMisc.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, FoMisc.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, FoMisc.fields.updatedBy)
		val qtyComped = new IntFieldValue(self, FoMisc.fields.qtyComped)
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
		@NullableInDatabase
		val itemId = new IntDatabaseField(self, "ITEM_ID")
		@NullableInDatabase
		val closeId = new IntDatabaseField(self, "CLOSE_ID")
		@NullableInDatabase
		val qtySold = new IntDatabaseField(self, "QTY_SOLD")
		@NullableInDatabase
		val totalPrice = new DoubleDatabaseField(self, "TOTAL_PRICE")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		@NullableInDatabase
		val qtyComped = new IntDatabaseField(self, "QTY_COMPED")
		@NullableInDatabase
		val totalComp = new DoubleDatabaseField(self, "TOTAL_COMP")
		val taxRate = new NullableDoubleDatabaseField(self, "TAX_RATE")
		val totalPretax = new NullableDoubleDatabaseField(self, "TOTAL_PRETAX")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}