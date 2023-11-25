package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class GuestPriv extends StorableClass(GuestPriv) {
	override object values extends ValuesObject {
		val orderId = new NullableIntFieldValue(self, GuestPriv.fields.orderId)
		val price = new DoubleFieldValue(self, GuestPriv.fields.price)
		val purchaseDate = new DateTimeFieldValue(self, GuestPriv.fields.purchaseDate)
		val startDate = new DateTimeFieldValue(self, GuestPriv.fields.startDate)
		val closeId = new NullableIntFieldValue(self, GuestPriv.fields.closeId)
		val voidCloseId = new NullableIntFieldValue(self, GuestPriv.fields.voidCloseId)
		val paymentLocation = new NullableStringFieldValue(self, GuestPriv.fields.paymentLocation)
		val paymentMedium = new NullableStringFieldValue(self, GuestPriv.fields.paymentMedium)
		val ccTransNum = new NullableDoubleFieldValue(self, GuestPriv.fields.ccTransNum)
		val createdOn = new DateTimeFieldValue(self, GuestPriv.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, GuestPriv.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, GuestPriv.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, GuestPriv.fields.updatedBy)
		val membershipId = new IntFieldValue(self, GuestPriv.fields.membershipId)
	}
}

object GuestPriv extends StorableObject[GuestPriv] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "GUEST_PRIVS"

	object fields extends FieldsObject {
		val orderId = new NullableIntDatabaseField(self, "ORDER_ID")
		@NullableInDatabase
		val price = new DoubleDatabaseField(self, "PRICE")
		@NullableInDatabase
		val purchaseDate = new DateTimeDatabaseField(self, "PURCHASE_DATE")
		@NullableInDatabase
		val startDate = new DateTimeDatabaseField(self, "START_DATE")
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
		val paymentLocation = new NullableStringDatabaseField(self, "PAYMENT_LOCATION", 50)
		val paymentMedium = new NullableStringDatabaseField(self, "PAYMENT_MEDIUM", 50)
		val ccTransNum = new NullableDoubleDatabaseField(self, "CC_TRANS_NUM")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val membershipId = new IntDatabaseField(self, "MEMBERSHIP_ID")
	}

	def primaryKey: IntDatabaseField = fields.membershipId
}