package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._

class GuestPriv extends StorableClass(GuestPriv) {
	object values extends ValuesObject {
		val membershipId = new IntFieldValue(self, GuestPriv.fields.membershipId)
		val orderId = new NullableIntFieldValue(self, GuestPriv.fields.orderId)
		val price = new NullableDoubleFieldValue(self, GuestPriv.fields.price)
		val purchaseDate = new NullableDateTimeFieldValue(self, GuestPriv.fields.purchaseDate)
		val startDate = new NullableDateFieldValue(self, GuestPriv.fields.startDate)
		val closeId = new NullableIntFieldValue(self, GuestPriv.fields.closeId)
		val voidCloseId = new NullableIntFieldValue(self, GuestPriv.fields.voidCloseId)
		val paymentLocation = new NullableStringFieldValue(self, GuestPriv.fields.paymentLocation)
		val paymentMedium = new NullableStringFieldValue(self, GuestPriv.fields.paymentMedium)
		val ccTransNum = new NullableIntFieldValue(self, GuestPriv.fields.ccTransNum)
//		val createdOn = new NullableDateTimeFieldValue(self, GuestPriv.fields.createdOn)
//		val createdBy = new NullableStringFieldValue(self, GuestPriv.fields.createdBy)
//		val updatedOn = new NullableDateTimeFieldValue(self, GuestPriv.fields.updatedOn)
//		val updatedBy = new NullableStringFieldValue(self, GuestPriv.fields.updatedBy)
	}
}

object GuestPriv extends StorableObject[GuestPriv] {
	val entityName: String = "GUEST_PRIVS"

	object fields extends FieldsObject {
		val membershipId = new IntDatabaseField(self, "MEMBERSHIP_ID")
		val orderId = new NullableIntDatabaseField(self, "ORDER_ID")
		val price = new NullableDoubleDatabaseField(self, "PRICE")
		val purchaseDate = new NullableDateTimeDatabaseField(self, "PURCHASE_DATE")
		val startDate = new NullableDateDatabaseField(self, "START_DATE")
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
		val paymentLocation = new NullableStringDatabaseField(self, "PAYMENT_LOCATION", 50)
		val paymentMedium = new NullableStringDatabaseField(self, "PAYMENT_MEDIUM", 50)
		val ccTransNum = new NullableIntDatabaseField(self, "CC_TRANS_NUM")
//		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
//		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
//		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
//		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.membershipId
}