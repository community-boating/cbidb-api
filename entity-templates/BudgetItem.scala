package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class BudgetItem extends StorableClass(BudgetItem) {
	object values extends ValuesObject {
		val itemId = new IntFieldValue(self, BudgetItem.fields.itemId)
		val itemName = new StringFieldValue(self, BudgetItem.fields.itemName)
		val createdOn = new NullableLocalDateTimeFieldValue(self, BudgetItem.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, BudgetItem.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, BudgetItem.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, BudgetItem.fields.updatedBy)
		val displayOrder = new NullableDoubleFieldValue(self, BudgetItem.fields.displayOrder)
	}
}

object BudgetItem extends StorableObject[BudgetItem] {
	val entityName: String = "BUDGET_ITEMS"

	object fields extends FieldsObject {
		val itemId = new IntDatabaseField(self, "ITEM_ID")
		val itemName = new StringDatabaseField(self, "ITEM_NAME", 200)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
	}

	def primaryKey: IntDatabaseField = fields.itemId
}