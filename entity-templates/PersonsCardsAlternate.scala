package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PersonsCardsAlternate extends StorableClass(PersonsCardsAlternate) {
	object values extends ValuesObject {
		val assignId = new IntFieldValue(self, PersonsCardsAlternate.fields.assignId)
		val personId = new NullableIntFieldValue(self, PersonsCardsAlternate.fields.personId)
		val issueDate = new NullableLocalDateTimeFieldValue(self, PersonsCardsAlternate.fields.issueDate)
		val createdOn = new NullableLocalDateTimeFieldValue(self, PersonsCardsAlternate.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, PersonsCardsAlternate.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, PersonsCardsAlternate.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, PersonsCardsAlternate.fields.updatedBy)
		val cardNum = new NullableStringFieldValue(self, PersonsCardsAlternate.fields.cardNum)
		val temp = new BooleanFIeldValue(self, PersonsCardsAlternate.fields.temp)
		val active = new NullableBooleanFIeldValue(self, PersonsCardsAlternate.fields.active)
		val price = new NullableDoubleFieldValue(self, PersonsCardsAlternate.fields.price)
		val paymentMedium = new NullableStringFieldValue(self, PersonsCardsAlternate.fields.paymentMedium)
		val ccTransNum = new NullableDoubleFieldValue(self, PersonsCardsAlternate.fields.ccTransNum)
		val paid = new NullableBooleanFIeldValue(self, PersonsCardsAlternate.fields.paid)
		val closeId = new NullableIntFieldValue(self, PersonsCardsAlternate.fields.closeId)
		val voidCloseId = new NullableIntFieldValue(self, PersonsCardsAlternate.fields.voidCloseId)
		val nonce = new NullableStringFieldValue(self, PersonsCardsAlternate.fields.nonce)
	}
}

object PersonsCardsAlternate extends StorableObject[PersonsCardsAlternate] {
	val entityName: String = "PERSONS_CARDS_ALTERNATE"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val personId = new NullableIntDatabaseField(self, "PERSON_ID")
		val issueDate = new NullableLocalDateTimeDatabaseField(self, "ISSUE_DATE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val cardNum = new NullableStringDatabaseField(self, "CARD_NUM", 50)
		val temp = new BooleanDatabaseField(self, "TEMP")
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val price = new NullableDoubleDatabaseField(self, "PRICE")
		val paymentMedium = new NullableStringDatabaseField(self, "PAYMENT_MEDIUM", 100)
		val ccTransNum = new NullableDoubleDatabaseField(self, "CC_TRANS_NUM")
		val paid = new NullableBooleanDatabaseField(self, "PAID")
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
		val nonce = new NullableStringDatabaseField(self, "NONCE", 10)
	}

	def primaryKey: IntDatabaseField = fields.assignId
}