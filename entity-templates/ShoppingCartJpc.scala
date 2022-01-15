package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ShoppingCartJpc extends StorableClass(ShoppingCartJpc) {
	object values extends ValuesObject {
		val itemId = new IntFieldValue(self, ShoppingCartJpc.fields.itemId)
		val personId = new NullableIntFieldValue(self, ShoppingCartJpc.fields.personId)
		val orderId = new NullableIntFieldValue(self, ShoppingCartJpc.fields.orderId)
		val instanceId = new NullableIntFieldValue(self, ShoppingCartJpc.fields.instanceId)
		val readyToBuy = new NullableBooleanFIeldValue(self, ShoppingCartJpc.fields.readyToBuy)
		val createdOn = new NullableLocalDateTimeFieldValue(self, ShoppingCartJpc.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ShoppingCartJpc.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, ShoppingCartJpc.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ShoppingCartJpc.fields.updatedBy)
		val price = new NullableDoubleFieldValue(self, ShoppingCartJpc.fields.price)
		val discountInstanceId = new NullableIntFieldValue(self, ShoppingCartJpc.fields.discountInstanceId)
		val discountAmt = new NullableDoubleFieldValue(self, ShoppingCartJpc.fields.discountAmt)
	}
}

object ShoppingCartJpc extends StorableObject[ShoppingCartJpc] {
	val entityName: String = "SHOPPING_CART_JPC"

	object fields extends FieldsObject {
		val itemId = new IntDatabaseField(self, "ITEM_ID")
		val personId = new NullableIntDatabaseField(self, "PERSON_ID")
		val orderId = new NullableIntDatabaseField(self, "ORDER_ID")
		val instanceId = new NullableIntDatabaseField(self, "INSTANCE_ID")
		val readyToBuy = new NullableBooleanDatabaseField(self, "READY_TO_BUY")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val price = new NullableDoubleDatabaseField(self, "PRICE")
		val discountInstanceId = new NullableIntDatabaseField(self, "DISCOUNT_INSTANCE_ID")
		val discountAmt = new NullableDoubleDatabaseField(self, "DISCOUNT_AMT")
	}

	def primaryKey: IntDatabaseField = fields.itemId
}