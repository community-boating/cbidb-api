package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ShoppingCartGc extends StorableClass(ShoppingCartGc) {
	object values extends ValuesObject {
		val itemId = new IntFieldValue(self, ShoppingCartGc.fields.itemId)
		val membershipTypeId = new NullableIntFieldValue(self, ShoppingCartGc.fields.membershipTypeId)
		val orderId = new NullableIntFieldValue(self, ShoppingCartGc.fields.orderId)
		val recipientEmail = new NullableStringFieldValue(self, ShoppingCartGc.fields.recipientEmail)
		val certId = new NullableIntFieldValue(self, ShoppingCartGc.fields.certId)
		val readyToBuy = new NullableBooleanFIeldValue(self, ShoppingCartGc.fields.readyToBuy)
		val createdOn = new NullableLocalDateTimeFieldValue(self, ShoppingCartGc.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ShoppingCartGc.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, ShoppingCartGc.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ShoppingCartGc.fields.updatedBy)
		val recipientNameFirst = new NullableStringFieldValue(self, ShoppingCartGc.fields.recipientNameFirst)
		val recipientNameLast = new NullableStringFieldValue(self, ShoppingCartGc.fields.recipientNameLast)
		val recipientAddr1 = new NullableStringFieldValue(self, ShoppingCartGc.fields.recipientAddr1)
		val recipientAddr2 = new NullableStringFieldValue(self, ShoppingCartGc.fields.recipientAddr2)
		val recipientCity = new NullableStringFieldValue(self, ShoppingCartGc.fields.recipientCity)
		val recipientState = new NullableStringFieldValue(self, ShoppingCartGc.fields.recipientState)
		val recipientZip = new NullableStringFieldValue(self, ShoppingCartGc.fields.recipientZip)
		val deliveryMethod = new BooleanFIeldValue(self, ShoppingCartGc.fields.deliveryMethod)
		val message = new NullableUnknownFieldType(self, ShoppingCartGc.fields.message)
		val whoseAddr = new NullableBooleanFIeldValue(self, ShoppingCartGc.fields.whoseAddr)
		val purchasePrice = new NullableDoubleFieldValue(self, ShoppingCartGc.fields.purchasePrice)
		val value = new NullableDoubleFieldValue(self, ShoppingCartGc.fields.value)
		val whoseEmail = new NullableBooleanFIeldValue(self, ShoppingCartGc.fields.whoseEmail)
		val discountInstanceId = new NullableIntFieldValue(self, ShoppingCartGc.fields.discountInstanceId)
	}
}

object ShoppingCartGc extends StorableObject[ShoppingCartGc] {
	val entityName: String = "SHOPPING_CART_GCS"

	object fields extends FieldsObject {
		val itemId = new IntDatabaseField(self, "ITEM_ID")
		val membershipTypeId = new NullableIntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
		val orderId = new NullableIntDatabaseField(self, "ORDER_ID")
		val recipientEmail = new NullableStringDatabaseField(self, "RECIPIENT_EMAIL", 500)
		val certId = new NullableIntDatabaseField(self, "CERT_ID")
		val readyToBuy = new NullableBooleanDatabaseField(self, "READY_TO_BUY")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val recipientNameFirst = new NullableStringDatabaseField(self, "RECIPIENT_NAME_FIRST", 200)
		val recipientNameLast = new NullableStringDatabaseField(self, "RECIPIENT_NAME_LAST", 200)
		val recipientAddr1 = new NullableStringDatabaseField(self, "RECIPIENT_ADDR_1", 500)
		val recipientAddr2 = new NullableStringDatabaseField(self, "RECIPIENT_ADDR_2", 500)
		val recipientCity = new NullableStringDatabaseField(self, "RECIPIENT_CITY", 100)
		val recipientState = new NullableStringDatabaseField(self, "RECIPIENT_STATE", 5)
		val recipientZip = new NullableStringDatabaseField(self, "RECIPIENT_ZIP", 20)
		val deliveryMethod = new BooleanDatabaseField(self, "DELIVERY_METHOD")
		val message = new NullableUnknownFieldType(self, "MESSAGE")
		val whoseAddr = new NullableBooleanDatabaseField(self, "WHOSE_ADDR")
		val purchasePrice = new NullableDoubleDatabaseField(self, "PURCHASE_PRICE")
		val value = new NullableDoubleDatabaseField(self, "VALUE")
		val whoseEmail = new NullableBooleanDatabaseField(self, "WHOSE_EMAIL")
		val discountInstanceId = new NullableIntDatabaseField(self, "DISCOUNT_INSTANCE_ID")
	}

	def primaryKey: IntDatabaseField = fields.itemId
}