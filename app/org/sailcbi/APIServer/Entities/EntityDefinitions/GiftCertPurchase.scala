package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class GiftCertPurchase extends StorableClass(GiftCertPurchase) {
	override object values extends ValuesObject {
		val certId = new NullableIntFieldValue(self, GiftCertPurchase.fields.certId)
		val purchaseDate = new DateTimeFieldValue(self, GiftCertPurchase.fields.purchaseDate)
		val purchasePrice = new DoubleFieldValue(self, GiftCertPurchase.fields.purchasePrice)
		val purchaseCloseId = new NullableIntFieldValue(self, GiftCertPurchase.fields.purchaseCloseId)
		val purchaseOrderId = new NullableIntFieldValue(self, GiftCertPurchase.fields.purchaseOrderId)
		val purchaseMemTypeId = new NullableIntFieldValue(self, GiftCertPurchase.fields.purchaseMemTypeId)
		val recipientNameFirst = new StringFieldValue(self, GiftCertPurchase.fields.recipientNameFirst)
		val recipientNameLast = new StringFieldValue(self, GiftCertPurchase.fields.recipientNameLast)
		val recipientEmail = new NullableStringFieldValue(self, GiftCertPurchase.fields.recipientEmail)
		val recipientAddr1 = new NullableStringFieldValue(self, GiftCertPurchase.fields.recipientAddr1)
		val recipientAddr2 = new NullableStringFieldValue(self, GiftCertPurchase.fields.recipientAddr2)
		val recipientCity = new NullableStringFieldValue(self, GiftCertPurchase.fields.recipientCity)
		val recipientState = new NullableStringFieldValue(self, GiftCertPurchase.fields.recipientState)
		val recipientZip = new NullableStringFieldValue(self, GiftCertPurchase.fields.recipientZip)
		val createdOn = new DateTimeFieldValue(self, GiftCertPurchase.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, GiftCertPurchase.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, GiftCertPurchase.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, GiftCertPurchase.fields.updatedBy)
		val foMadeDatetime = new NullableDateTimeFieldValue(self, GiftCertPurchase.fields.foMadeDatetime)
		val purchaserId = new IntFieldValue(self, GiftCertPurchase.fields.purchaserId)
		val deliveryMethod = new StringFieldValue(self, GiftCertPurchase.fields.deliveryMethod)
		val message = new NullableStringFieldValue(self, GiftCertPurchase.fields.message)
		val purchaseId = new IntFieldValue(self, GiftCertPurchase.fields.purchaseId)
		val whoseAddr = new NullableStringFieldValue(self, GiftCertPurchase.fields.whoseAddr)
		val expirationDate = new DateTimeFieldValue(self, GiftCertPurchase.fields.expirationDate)
		val discountInstanceId = new NullableIntFieldValue(self, GiftCertPurchase.fields.discountInstanceId)
		val purchaseValue = new NullableDoubleFieldValue(self, GiftCertPurchase.fields.purchaseValue)
		val foMadeBy = new NullableStringFieldValue(self, GiftCertPurchase.fields.foMadeBy)
		val soldBy = new NullableStringFieldValue(self, GiftCertPurchase.fields.soldBy)
		val whoseEmail = new NullableStringFieldValue(self, GiftCertPurchase.fields.whoseEmail)
		val voidCloseId = new NullableIntFieldValue(self, GiftCertPurchase.fields.voidCloseId)
		val discountAmt = new NullableDoubleFieldValue(self, GiftCertPurchase.fields.discountAmt)
	}
}

object GiftCertPurchase extends StorableObject[GiftCertPurchase] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "GIFT_CERT_PURCHASES"

	object fields extends FieldsObject {
		val certId = new NullableIntDatabaseField(self, "CERT_ID")
		@NullableInDatabase
		val purchaseDate = new DateTimeDatabaseField(self, "PURCHASE_DATE")
		@NullableInDatabase
		val purchasePrice = new DoubleDatabaseField(self, "PURCHASE_PRICE")
		val purchaseCloseId = new NullableIntDatabaseField(self, "PURCHASE_CLOSE_ID")
		val purchaseOrderId = new NullableIntDatabaseField(self, "PURCHASE_ORDER_ID")
		val purchaseMemTypeId = new NullableIntDatabaseField(self, "PURCHASE_MEM_TYPE_ID")
		@NullableInDatabase
		val recipientNameFirst = new StringDatabaseField(self, "RECIPIENT_NAME_FIRST", 100)
		@NullableInDatabase
		val recipientNameLast = new StringDatabaseField(self, "RECIPIENT_NAME_LAST", 100)
		val recipientEmail = new NullableStringDatabaseField(self, "RECIPIENT_EMAIL", 500)
		val recipientAddr1 = new NullableStringDatabaseField(self, "RECIPIENT_ADDR_1", 100)
		val recipientAddr2 = new NullableStringDatabaseField(self, "RECIPIENT_ADDR_2", 100)
		val recipientCity = new NullableStringDatabaseField(self, "RECIPIENT_CITY", 50)
		val recipientState = new NullableStringDatabaseField(self, "RECIPIENT_STATE", 5)
		val recipientZip = new NullableStringDatabaseField(self, "RECIPIENT_ZIP", 20)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val foMadeDatetime = new NullableDateTimeDatabaseField(self, "FO_MADE_DATETIME")
		@NullableInDatabase
		val purchaserId = new IntDatabaseField(self, "PURCHASER_ID")
		val deliveryMethod = new StringDatabaseField(self, "DELIVERY_METHOD", 1)
		val message = new NullableStringDatabaseField(self, "MESSAGE", -1)
		val purchaseId = new IntDatabaseField(self, "PURCHASE_ID")
		val whoseAddr = new NullableStringDatabaseField(self, "WHOSE_ADDR", 1)
		val expirationDate = new DateTimeDatabaseField(self, "EXPIRATION_DATE")
		val discountInstanceId = new NullableIntDatabaseField(self, "DISCOUNT_INSTANCE_ID")
		val purchaseValue = new NullableDoubleDatabaseField(self, "PURCHASE_VALUE")
		val foMadeBy = new NullableStringDatabaseField(self, "FO_MADE_BY", 50)
		val soldBy = new NullableStringDatabaseField(self, "SOLD_BY", 50)
		val whoseEmail = new NullableStringDatabaseField(self, "WHOSE_EMAIL", 1)
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
		val discountAmt = new NullableDoubleDatabaseField(self, "DISCOUNT_AMT")
	}

	def primaryKey: IntDatabaseField = fields.purchaseId
}