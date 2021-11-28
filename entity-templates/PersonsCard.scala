package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PersonsCard extends StorableClass(PersonsCard) {
	object values extends ValuesObject {
		val assignId = new IntFieldValue(self, PersonsCard.fields.assignId)
		val personId = new NullableIntFieldValue(self, PersonsCard.fields.personId)
		val issueDate = new NullableLocalDateTimeFieldValue(self, PersonsCard.fields.issueDate)
		val createdOn = new NullableLocalDateTimeFieldValue(self, PersonsCard.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, PersonsCard.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, PersonsCard.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, PersonsCard.fields.updatedBy)
		val cardNum = new NullableStringFieldValue(self, PersonsCard.fields.cardNum)
		val temp = new BooleanFIeldValue(self, PersonsCard.fields.temp)
		val active = new NullableBooleanFIeldValue(self, PersonsCard.fields.active)
		val price = new NullableDoubleFieldValue(self, PersonsCard.fields.price)
		val paymentMedium = new NullableStringFieldValue(self, PersonsCard.fields.paymentMedium)
		val ccTransNum = new NullableDoubleFieldValue(self, PersonsCard.fields.ccTransNum)
		val paid = new NullableBooleanFIeldValue(self, PersonsCard.fields.paid)
		val closeId = new NullableIntFieldValue(self, PersonsCard.fields.closeId)
		val voidCloseId = new NullableIntFieldValue(self, PersonsCard.fields.voidCloseId)
		val nonce = new NullableStringFieldValue(self, PersonsCard.fields.nonce)
	}
}

object PersonsCard extends StorableObject[PersonsCard] {
	val entityName: String = "PERSONS_CARDS"

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