package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class GuestPriv extends StorableClass(GuestPriv) {
	object values extends ValuesObject {
		val orderId = new NullableIntFieldValue(self, GuestPriv.fields.orderId)
		val price = new NullableDoubleFieldValue(self, GuestPriv.fields.price)
		val purchaseDate = new NullableLocalDateTimeFieldValue(self, GuestPriv.fields.purchaseDate)
		val startDate = new NullableLocalDateTimeFieldValue(self, GuestPriv.fields.startDate)
		val closeId = new NullableIntFieldValue(self, GuestPriv.fields.closeId)
		val voidCloseId = new NullableIntFieldValue(self, GuestPriv.fields.voidCloseId)
		val paymentLocation = new NullableStringFieldValue(self, GuestPriv.fields.paymentLocation)
		val paymentMedium = new NullableStringFieldValue(self, GuestPriv.fields.paymentMedium)
		val ccTransNum = new NullableDoubleFieldValue(self, GuestPriv.fields.ccTransNum)
		val createdOn = new NullableLocalDateTimeFieldValue(self, GuestPriv.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, GuestPriv.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, GuestPriv.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, GuestPriv.fields.updatedBy)
		val membershipId = new IntFieldValue(self, GuestPriv.fields.membershipId)
	}
}

object GuestPriv extends StorableObject[GuestPriv] {
	val entityName: String = "GUEST_PRIVS"

	object fields extends FieldsObject {
		val orderId = new NullableIntDatabaseField(self, "ORDER_ID")
		val price = new NullableDoubleDatabaseField(self, "PRICE")
		val purchaseDate = new NullableLocalDateTimeDatabaseField(self, "PURCHASE_DATE")
		val startDate = new NullableLocalDateTimeDatabaseField(self, "START_DATE")
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
		val paymentLocation = new NullableStringDatabaseField(self, "PAYMENT_LOCATION", 50)
		val paymentMedium = new NullableStringDatabaseField(self, "PAYMENT_MEDIUM", 50)
		val ccTransNum = new NullableDoubleDatabaseField(self, "CC_TRANS_NUM")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val membershipId = new IntDatabaseField(self, "MEMBERSHIP_ID")
	}

	def primaryKey: IntDatabaseField = fields.membershipId
}