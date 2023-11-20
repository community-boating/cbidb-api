package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PersonsCardsAlternate extends StorableClass(PersonsCardsAlternate) {
	override object values extends ValuesObject {
		val assignId = new IntFieldValue(self, PersonsCardsAlternate.fields.assignId)
		val personId = new NullableIntFieldValue(self, PersonsCardsAlternate.fields.personId)
		val issueDate = new NullableDateTimeFieldValue(self, PersonsCardsAlternate.fields.issueDate)
		val createdOn = new DateTimeFieldValue(self, PersonsCardsAlternate.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, PersonsCardsAlternate.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, PersonsCardsAlternate.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, PersonsCardsAlternate.fields.updatedBy)
		val cardNum = new NullableStringFieldValue(self, PersonsCardsAlternate.fields.cardNum)
		val temp = new BooleanFieldValue(self, PersonsCardsAlternate.fields.temp)
		val active = new NullableBooleanFieldValue(self, PersonsCardsAlternate.fields.active)
		val ccTransNum = new NullableDoubleFieldValue(self, PersonsCardsAlternate.fields.ccTransNum)
		val paymentMedium = new NullableStringFieldValue(self, PersonsCardsAlternate.fields.paymentMedium)
		val price = new NullableDoubleFieldValue(self, PersonsCardsAlternate.fields.price)
		val closeId = new NullableIntFieldValue(self, PersonsCardsAlternate.fields.closeId)
		val paid = new NullableBooleanFieldValue(self, PersonsCardsAlternate.fields.paid)
		val voidCloseId = new NullableIntFieldValue(self, PersonsCardsAlternate.fields.voidCloseId)
		val nonce = new NullableStringFieldValue(self, PersonsCardsAlternate.fields.nonce)
	}
}

object PersonsCardsAlternate extends StorableObject[PersonsCardsAlternate] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "PERSONS_CARDS_ALTERNATE"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val personId = new NullableIntDatabaseField(self, "PERSON_ID")
		val issueDate = new NullableDateTimeDatabaseField(self, "ISSUE_DATE")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val cardNum = new NullableStringDatabaseField(self, "CARD_NUM", 50)
		val temp = new BooleanDatabaseField(self, "TEMP", false)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val ccTransNum = new NullableDoubleDatabaseField(self, "CC_TRANS_NUM")
		val paymentMedium = new NullableStringDatabaseField(self, "PAYMENT_MEDIUM", 100)
		val price = new NullableDoubleDatabaseField(self, "PRICE")
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val paid = new NullableBooleanDatabaseField(self, "PAID")
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
		val nonce = new NullableStringDatabaseField(self, "NONCE", 10)
	}

	def primaryKey: IntDatabaseField = fields.assignId
}