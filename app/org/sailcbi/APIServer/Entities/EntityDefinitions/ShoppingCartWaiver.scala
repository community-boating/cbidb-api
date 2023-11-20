package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ShoppingCartWaiver extends StorableClass(ShoppingCartWaiver) {
	override object values extends ValuesObject {
		val itemId = new IntFieldValue(self, ShoppingCartWaiver.fields.itemId)
		val orderId = new IntFieldValue(self, ShoppingCartWaiver.fields.orderId)
		val personId = new NullableIntFieldValue(self, ShoppingCartWaiver.fields.personId)
		val createdOn = new DateTimeFieldValue(self, ShoppingCartWaiver.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ShoppingCartWaiver.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, ShoppingCartWaiver.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ShoppingCartWaiver.fields.updatedBy)
		val price = new DoubleFieldValue(self, ShoppingCartWaiver.fields.price)
	}
}

object ShoppingCartWaiver extends StorableObject[ShoppingCartWaiver] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "SHOPPING_CART_WAIVERS"

	object fields extends FieldsObject {
		val itemId = new IntDatabaseField(self, "ITEM_ID")
		val orderId = new IntDatabaseField(self, "ORDER_ID")
		val personId = new NullableIntDatabaseField(self, "PERSON_ID")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val price = new DoubleDatabaseField(self, "PRICE")
	}

	def primaryKey: IntDatabaseField = fields.itemId
}