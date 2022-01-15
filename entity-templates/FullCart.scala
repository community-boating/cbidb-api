package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FullCart extends StorableClass(FullCart) {
	object values extends ValuesObject {
		val itemName = new NullableStringFieldValue(self, FullCart.fields.itemName)
		val itemId = new NullableIntFieldValue(self, FullCart.fields.itemId)
		val itemType = new NullableStringFieldValue(self, FullCart.fields.itemType)
		val nameFirst = new NullableStringFieldValue(self, FullCart.fields.nameFirst)
		val nameLast = new NullableStringFieldValue(self, FullCart.fields.nameLast)
		val price = new NullableDoubleFieldValue(self, FullCart.fields.price)
		val displayOrder = new NullableDoubleFieldValue(self, FullCart.fields.displayOrder)
		val orderId = new NullableIntFieldValue(self, FullCart.fields.orderId)
		val fundId = new NullableIntFieldValue(self, FullCart.fields.fundId)
	}
}

object FullCart extends StorableObject[FullCart] {
	val entityName: String = "FULL_CART"

	object fields extends FieldsObject {
		val itemName = new NullableStringDatabaseField(self, "ITEM_NAME", 227)
		val itemId = new NullableIntDatabaseField(self, "ITEM_ID")
		val itemType = new NullableStringDatabaseField(self, "ITEM_TYPE", 25)
		val nameFirst = new NullableStringDatabaseField(self, "NAME_FIRST", 200)
		val nameLast = new NullableStringDatabaseField(self, "NAME_LAST", 200)
		val price = new NullableDoubleDatabaseField(self, "PRICE")
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
		val orderId = new NullableIntDatabaseField(self, "ORDER_ID")
		val fundId = new NullableIntDatabaseField(self, "FUND_ID")
	}
}