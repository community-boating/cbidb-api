package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class FoItemType extends StorableClass(FoItemType) {
	override object values extends ValuesObject {
		val rowId = new IntFieldValue(self, FoItemType.fields.rowId)
		val typeId = new IntFieldValue(self, FoItemType.fields.typeId)
		val closeId = new IntFieldValue(self, FoItemType.fields.closeId)
		val discountsTotal = new DoubleFieldValue(self, FoItemType.fields.discountsTotal)
		val qtySold = new DoubleFieldValue(self, FoItemType.fields.qtySold)
		val qtyComped = new DoubleFieldValue(self, FoItemType.fields.qtyComped)
		val createdOn = new DateTimeFieldValue(self, FoItemType.fields.createdOn)
		val createdBy = new StringFieldValue(self, FoItemType.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, FoItemType.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, FoItemType.fields.updatedBy)
		val unitPrice = new DoubleFieldValue(self, FoItemType.fields.unitPrice)
		val taxRate = new NullableDoubleFieldValue(self, FoItemType.fields.taxRate)
	}
}

object FoItemType extends StorableObject[FoItemType] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "FO_ITEM_TYPES"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		@NullableInDatabase
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		@NullableInDatabase
		val closeId = new IntDatabaseField(self, "CLOSE_ID")
		@NullableInDatabase
		val discountsTotal = new DoubleDatabaseField(self, "DISCOUNTS_TOTAL")
		@NullableInDatabase
		val qtySold = new DoubleDatabaseField(self, "QTY_SOLD")
		@NullableInDatabase
		val qtyComped = new DoubleDatabaseField(self, "QTY_COMPED")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		@NullableInDatabase
		val unitPrice = new DoubleDatabaseField(self, "UNIT_PRICE")
		val taxRate = new NullableDoubleDatabaseField(self, "TAX_RATE")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}