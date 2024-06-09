package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class BudgetItem extends StorableClass(BudgetItem) {
	override object values extends ValuesObject {
		val itemId = new IntFieldValue(self, BudgetItem.fields.itemId)
		val itemName = new StringFieldValue(self, BudgetItem.fields.itemName)
		val createdOn = new NullableDateTimeFieldValue(self, BudgetItem.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, BudgetItem.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, BudgetItem.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, BudgetItem.fields.updatedBy)
		val displayOrder = new DoubleFieldValue(self, BudgetItem.fields.displayOrder)
	}
}

object BudgetItem extends StorableObject[BudgetItem] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "BUDGET_ITEMS"

	object fields extends FieldsObject {
		val itemId = new IntDatabaseField(self, "ITEM_ID")
		val itemName = new StringDatabaseField(self, "ITEM_NAME", 200)
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		@NullableInDatabase
		val displayOrder = new DoubleDatabaseField(self, "DISPLAY_ORDER")
	}

	def primaryKey: IntDatabaseField = fields.itemId
}