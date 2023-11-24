package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class PersonCard extends StorableClass(PersonCard) {
	override object values extends ValuesObject {
		val assignId = new IntFieldValue(self, PersonCard.fields.assignId)
		val personId = new IntFieldValue(self, PersonCard.fields.personId)
		val issueDate = new DateTimeFieldValue(self, PersonCard.fields.issueDate)
		val createdOn = new DateTimeFieldValue(self, PersonCard.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, PersonCard.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, PersonCard.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, PersonCard.fields.updatedBy)
		val cardNum = new StringFieldValue(self, PersonCard.fields.cardNum)
		val temp = new BooleanFieldValue(self, PersonCard.fields.temp)
		val active = new BooleanFieldValue(self, PersonCard.fields.active)
		val ccTransNum = new NullableDoubleFieldValue(self, PersonCard.fields.ccTransNum)
		val paymentMedium = new NullableStringFieldValue(self, PersonCard.fields.paymentMedium)
		val price = new NullableDoubleFieldValue(self, PersonCard.fields.price)
		val closeId = new NullableIntFieldValue(self, PersonCard.fields.closeId)
		val paid = new NullableStringFieldValue(self, PersonCard.fields.paid)
		val voidCloseId = new NullableIntFieldValue(self, PersonCard.fields.voidCloseId)
		val nonce = new NullableStringFieldValue(self, PersonCard.fields.nonce)
	}
}

object PersonCard extends StorableObject[PersonCard] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "PERSONS_CARDS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		@NullableInDatabase
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val issueDate = new DateTimeDatabaseField(self, "ISSUE_DATE")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val cardNum = new StringDatabaseField(self, "CARD_NUM", 50)
		val temp = new BooleanDatabaseField(self, "TEMP", false)
		@NullableInDatabase
		val active = new BooleanDatabaseField(self, "ACTIVE", false)
		val ccTransNum = new NullableDoubleDatabaseField(self, "CC_TRANS_NUM")
		val paymentMedium = new NullableStringDatabaseField(self, "PAYMENT_MEDIUM", 100)
		val price = new NullableDoubleDatabaseField(self, "PRICE")
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val paid = new NullableStringDatabaseField(self, "PAID", 1)
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
		val nonce = new NullableStringDatabaseField(self, "NONCE", 10)
	}

	def primaryKey: IntDatabaseField = fields.assignId
}