package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class GiftCertPurchase extends StorableClass(GiftCertPurchase) {
	object values extends ValuesObject {
		val certId = new NullableIntFieldValue(self, GiftCertPurchase.fields.certId)
		val purchaseDate = new NullableLocalDateTimeFieldValue(self, GiftCertPurchase.fields.purchaseDate)
		val purchasePrice = new NullableDoubleFieldValue(self, GiftCertPurchase.fields.purchasePrice)
		val purchaseCloseId = new NullableIntFieldValue(self, GiftCertPurchase.fields.purchaseCloseId)
		val purchaseOrderId = new NullableIntFieldValue(self, GiftCertPurchase.fields.purchaseOrderId)
		val purchaseMemTypeId = new NullableIntFieldValue(self, GiftCertPurchase.fields.purchaseMemTypeId)
		val recipientNameFirst = new NullableStringFieldValue(self, GiftCertPurchase.fields.recipientNameFirst)
		val recipientNameLast = new NullableStringFieldValue(self, GiftCertPurchase.fields.recipientNameLast)
		val recipientEmail = new NullableStringFieldValue(self, GiftCertPurchase.fields.recipientEmail)
		val recipientAddr1 = new NullableStringFieldValue(self, GiftCertPurchase.fields.recipientAddr1)
		val recipientAddr2 = new NullableStringFieldValue(self, GiftCertPurchase.fields.recipientAddr2)
		val recipientCity = new NullableStringFieldValue(self, GiftCertPurchase.fields.recipientCity)
		val recipientState = new NullableStringFieldValue(self, GiftCertPurchase.fields.recipientState)
		val recipientZip = new NullableStringFieldValue(self, GiftCertPurchase.fields.recipientZip)
		val createdOn = new NullableLocalDateTimeFieldValue(self, GiftCertPurchase.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, GiftCertPurchase.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, GiftCertPurchase.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, GiftCertPurchase.fields.updatedBy)
		val foMadeDatetime = new NullableLocalDateTimeFieldValue(self, GiftCertPurchase.fields.foMadeDatetime)
		val purchaserId = new NullableIntFieldValue(self, GiftCertPurchase.fields.purchaserId)
		val deliveryMethod = new BooleanFIeldValue(self, GiftCertPurchase.fields.deliveryMethod)
		val message = new NullableUnknownFieldType(self, GiftCertPurchase.fields.message)
		val purchaseId = new IntFieldValue(self, GiftCertPurchase.fields.purchaseId)
		val whoseAddr = new NullableBooleanFIeldValue(self, GiftCertPurchase.fields.whoseAddr)
		val expirationDate = new LocalDateTimeFieldValue(self, GiftCertPurchase.fields.expirationDate)
		val purchaseValue = new NullableDoubleFieldValue(self, GiftCertPurchase.fields.purchaseValue)
		val discountInstanceId = new NullableIntFieldValue(self, GiftCertPurchase.fields.discountInstanceId)
		val soldBy = new NullableStringFieldValue(self, GiftCertPurchase.fields.soldBy)
		val foMadeBy = new NullableStringFieldValue(self, GiftCertPurchase.fields.foMadeBy)
		val whoseEmail = new NullableBooleanFIeldValue(self, GiftCertPurchase.fields.whoseEmail)
		val voidCloseId = new NullableIntFieldValue(self, GiftCertPurchase.fields.voidCloseId)
		val discountAmt = new NullableDoubleFieldValue(self, GiftCertPurchase.fields.discountAmt)
	}
}

object GiftCertPurchase extends StorableObject[GiftCertPurchase] {
	val entityName: String = "GIFT_CERT_PURCHASES"

	object fields extends FieldsObject {
		val certId = new NullableIntDatabaseField(self, "CERT_ID")
		val purchaseDate = new NullableLocalDateTimeDatabaseField(self, "PURCHASE_DATE")
		val purchasePrice = new NullableDoubleDatabaseField(self, "PURCHASE_PRICE")
		val purchaseCloseId = new NullableIntDatabaseField(self, "PURCHASE_CLOSE_ID")
		val purchaseOrderId = new NullableIntDatabaseField(self, "PURCHASE_ORDER_ID")
		val purchaseMemTypeId = new NullableIntDatabaseField(self, "PURCHASE_MEM_TYPE_ID")
		val recipientNameFirst = new NullableStringDatabaseField(self, "RECIPIENT_NAME_FIRST", 100)
		val recipientNameLast = new NullableStringDatabaseField(self, "RECIPIENT_NAME_LAST", 100)
		val recipientEmail = new NullableStringDatabaseField(self, "RECIPIENT_EMAIL", 500)
		val recipientAddr1 = new NullableStringDatabaseField(self, "RECIPIENT_ADDR_1", 100)
		val recipientAddr2 = new NullableStringDatabaseField(self, "RECIPIENT_ADDR_2", 100)
		val recipientCity = new NullableStringDatabaseField(self, "RECIPIENT_CITY", 50)
		val recipientState = new NullableStringDatabaseField(self, "RECIPIENT_STATE", 5)
		val recipientZip = new NullableStringDatabaseField(self, "RECIPIENT_ZIP", 20)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val foMadeDatetime = new NullableLocalDateTimeDatabaseField(self, "FO_MADE_DATETIME")
		val purchaserId = new NullableIntDatabaseField(self, "PURCHASER_ID")
		val deliveryMethod = new BooleanDatabaseField(self, "DELIVERY_METHOD")
		val message = new NullableUnknownFieldType(self, "MESSAGE")
		val purchaseId = new IntDatabaseField(self, "PURCHASE_ID")
		val whoseAddr = new NullableBooleanDatabaseField(self, "WHOSE_ADDR")
		val expirationDate = new LocalDateTimeDatabaseField(self, "EXPIRATION_DATE")
		val purchaseValue = new NullableDoubleDatabaseField(self, "PURCHASE_VALUE")
		val discountInstanceId = new NullableIntDatabaseField(self, "DISCOUNT_INSTANCE_ID")
		val soldBy = new NullableStringDatabaseField(self, "SOLD_BY", 50)
		val foMadeBy = new NullableStringDatabaseField(self, "FO_MADE_BY", 50)
		val whoseEmail = new NullableBooleanDatabaseField(self, "WHOSE_EMAIL")
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
		val discountAmt = new NullableDoubleDatabaseField(self, "DISCOUNT_AMT")
	}

	def primaryKey: IntDatabaseField = fields.purchaseId
}