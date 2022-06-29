package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._

class PersonCard extends StorableClass(PersonCard) {
	object values extends ValuesObject {
		val assignId = new IntFieldValue(self, PersonCard.fields.assignId)
		val personId = new IntFieldValue(self, PersonCard.fields.personId)
		val issueDate = new NullableDateTimeFieldValue(self, PersonCard.fields.issueDate)
		val cardNum = new NullableStringFieldValue(self, PersonCard.fields.cardNum)
		val active = new BooleanFieldValue(self, PersonCard.fields.active)
		val price = new NullableDoubleFieldValue(self, PersonCard.fields.price)
		val paymentMedium = new NullableStringFieldValue(self, PersonCard.fields.paymentMedium)
		val ccTransNum = new NullableDoubleFieldValue(self, PersonCard.fields.ccTransNum)
		val paid = new NullableStringFieldValue(self, PersonCard.fields.paid)
		val closeId = new NullableIntFieldValue(self, PersonCard.fields.closeId)
		val voidCloseId = new NullableIntFieldValue(self, PersonCard.fields.voidCloseId)
		val nonce = new NullableStringFieldValue(self, PersonCard.fields.nonce)
	}
}

object PersonCard extends StorableObject[PersonCard] {
	val entityName: String = "PERSONS_CARDS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val issueDate = new NullableDateTimeDatabaseField(self, "ISSUE_DATE")
		val cardNum = new NullableStringDatabaseField(self, "CARD_NUM", 50)
		val active = new BooleanDatabaseField(self, "ACTIVE")
		val price = new NullableDoubleDatabaseField(self, "PRICE")
		val paymentMedium = new NullableStringDatabaseField(self, "PAYMENT_MEDIUM", 100)
		val ccTransNum = new NullableDoubleDatabaseField(self, "CC_TRANS_NUM")
		val paid = new NullableStringDatabaseField(self, "PAID", 1)
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
		val nonce = new NullableStringDatabaseField(self, "NONCE", 10)
	}

	def primaryKey: IntDatabaseField = fields.assignId
}