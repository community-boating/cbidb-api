package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class DamageWaiver extends StorableClass(DamageWaiver) {
	override object values extends ValuesObject {
		val waiverId = new IntFieldValue(self, DamageWaiver.fields.waiverId)
		val personId = new IntFieldValue(self, DamageWaiver.fields.personId)
		val orderId = new NullableIntFieldValue(self, DamageWaiver.fields.orderId)
		val price = new NullableDoubleFieldValue(self, DamageWaiver.fields.price)
		val purchaseDate = new DateTimeFieldValue(self, DamageWaiver.fields.purchaseDate)
		val startDate = new DateTimeFieldValue(self, DamageWaiver.fields.startDate)
		val expirationDate = new NullableDateTimeFieldValue(self, DamageWaiver.fields.expirationDate)
		val createdOn = new DateTimeFieldValue(self, DamageWaiver.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, DamageWaiver.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, DamageWaiver.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, DamageWaiver.fields.updatedBy)
		val closeId = new NullableIntFieldValue(self, DamageWaiver.fields.closeId)
		val ccTransNum = new NullableDoubleFieldValue(self, DamageWaiver.fields.ccTransNum)
		val paymentLocation = new NullableStringFieldValue(self, DamageWaiver.fields.paymentLocation)
		val paymentMedium = new NullableStringFieldValue(self, DamageWaiver.fields.paymentMedium)
		val voidCloseId = new NullableIntFieldValue(self, DamageWaiver.fields.voidCloseId)
	}
}

object DamageWaiver extends StorableObject[DamageWaiver] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "DAMAGE_WAIVERS"

	object fields extends FieldsObject {
		val waiverId = new IntDatabaseField(self, "WAIVER_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val orderId = new NullableIntDatabaseField(self, "ORDER_ID")
		val price = new NullableDoubleDatabaseField(self, "PRICE")
		@NullableInDatabase
		val purchaseDate = new DateTimeDatabaseField(self, "PURCHASE_DATE")
		@NullableInDatabase
		val startDate = new DateTimeDatabaseField(self, "START_DATE")
		val expirationDate = new NullableDateTimeDatabaseField(self, "EXPIRATION_DATE")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val ccTransNum = new NullableDoubleDatabaseField(self, "CC_TRANS_NUM")
		val paymentLocation = new NullableStringDatabaseField(self, "PAYMENT_LOCATION", 100)
		val paymentMedium = new NullableStringDatabaseField(self, "PAYMENT_MEDIUM", 100)
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
	}

	def primaryKey: IntDatabaseField = fields.waiverId
}