package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PersonsMembership extends StorableClass(PersonsMembership) {
	object values extends ValuesObject {
		val assignId = new IntFieldValue(self, PersonsMembership.fields.assignId)
		val personId = new IntFieldValue(self, PersonsMembership.fields.personId)
		val membershipTypeId = new IntFieldValue(self, PersonsMembership.fields.membershipTypeId)
		val purchaseDate = new NullableLocalDateTimeFieldValue(self, PersonsMembership.fields.purchaseDate)
		val startDate = new NullableLocalDateTimeFieldValue(self, PersonsMembership.fields.startDate)
		val expirationDate = new NullableLocalDateTimeFieldValue(self, PersonsMembership.fields.expirationDate)
		val schoolId = new NullableIntFieldValue(self, PersonsMembership.fields.schoolId)
		val orderId = new NullableIntFieldValue(self, PersonsMembership.fields.orderId)
		val price = new DoubleFieldValue(self, PersonsMembership.fields.price)
		val createdOn = new NullableLocalDateTimeFieldValue(self, PersonsMembership.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, PersonsMembership.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, PersonsMembership.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, PersonsMembership.fields.updatedBy)
		val temp = new BooleanFIeldValue(self, PersonsMembership.fields.temp)
		val paymentLocation = new NullableStringFieldValue(self, PersonsMembership.fields.paymentLocation)
		val paymentMedium = new NullableStringFieldValue(self, PersonsMembership.fields.paymentMedium)
		val oldCardNum = new NullableStringFieldValue(self, PersonsMembership.fields.oldCardNum)
		val oldCompReason = new NullableStringFieldValue(self, PersonsMembership.fields.oldCompReason)
		val oldDmgWaiver = new NullableBooleanFIeldValue(self, PersonsMembership.fields.oldDmgWaiver)
		val closeId = new NullableIntFieldValue(self, PersonsMembership.fields.closeId)
		val ccTransNum = new NullableDoubleFieldValue(self, PersonsMembership.fields.ccTransNum)
		val groupId = new NullableIntFieldValue(self, PersonsMembership.fields.groupId)
		val discountInstanceId = new NullableIntFieldValue(self, PersonsMembership.fields.discountInstanceId)
		val notes = new NullableStringFieldValue(self, PersonsMembership.fields.notes)
		val discountId = new NullableIntFieldValue(self, PersonsMembership.fields.discountId)
		val voidCloseId = new NullableIntFieldValue(self, PersonsMembership.fields.voidCloseId)
		val discountAmt = new NullableDoubleFieldValue(self, PersonsMembership.fields.discountAmt)
		val taxRate = new NullableDoubleFieldValue(self, PersonsMembership.fields.taxRate)
		val pretaxPrice = new NullableDoubleFieldValue(self, PersonsMembership.fields.pretaxPrice)
		val uapGroupId = new NullableIntFieldValue(self, PersonsMembership.fields.uapGroupId)
		val ignoreDiscountFrozen = new NullableBooleanFIeldValue(self, PersonsMembership.fields.ignoreDiscountFrozen)
		val covidDays = new NullableDoubleFieldValue(self, PersonsMembership.fields.covidDays)
		val covidEmailSent = new NullableBooleanFIeldValue(self, PersonsMembership.fields.covidEmailSent)
		val originalExpirationDate = new NullableLocalDateTimeFieldValue(self, PersonsMembership.fields.originalExpirationDate)
	}
}

object PersonsMembership extends StorableObject[PersonsMembership] {
	val entityName: String = "PERSONS_MEMBERSHIPS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val membershipTypeId = new IntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
		val purchaseDate = new NullableLocalDateTimeDatabaseField(self, "PURCHASE_DATE")
		val startDate = new NullableLocalDateTimeDatabaseField(self, "START_DATE")
		val expirationDate = new NullableLocalDateTimeDatabaseField(self, "EXPIRATION_DATE")
		val schoolId = new NullableIntDatabaseField(self, "SCHOOL_ID")
		val orderId = new NullableIntDatabaseField(self, "ORDER_ID")
		val price = new DoubleDatabaseField(self, "PRICE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val temp = new BooleanDatabaseField(self, "TEMP")
		val paymentLocation = new NullableStringDatabaseField(self, "PAYMENT_LOCATION", 50)
		val paymentMedium = new NullableStringDatabaseField(self, "PAYMENT_MEDIUM", 50)
		val oldCardNum = new NullableStringDatabaseField(self, "OLD_CARD_NUM", 100)
		val oldCompReason = new NullableStringDatabaseField(self, "OLD_COMP_REASON", 500)
		val oldDmgWaiver = new NullableBooleanDatabaseField(self, "OLD_DMG_WAIVER")
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val ccTransNum = new NullableDoubleDatabaseField(self, "CC_TRANS_NUM")
		val groupId = new NullableIntDatabaseField(self, "GROUP_ID")
		val discountInstanceId = new NullableIntDatabaseField(self, "DISCOUNT_INSTANCE_ID")
		val notes = new NullableStringDatabaseField(self, "NOTES", 4000)
		val discountId = new NullableIntDatabaseField(self, "DISCOUNT_ID")
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
		val discountAmt = new NullableDoubleDatabaseField(self, "DISCOUNT_AMT")
		val taxRate = new NullableDoubleDatabaseField(self, "TAX_RATE")
		val pretaxPrice = new NullableDoubleDatabaseField(self, "PRETAX_PRICE")
		val uapGroupId = new NullableIntDatabaseField(self, "UAP_GROUP_ID")
		val ignoreDiscountFrozen = new NullableBooleanDatabaseField(self, "IGNORE_DISCOUNT_FROZEN")
		val covidDays = new NullableDoubleDatabaseField(self, "COVID_DAYS")
		val covidEmailSent = new NullableBooleanDatabaseField(self, "COVID_EMAIL_SENT")
		val originalExpirationDate = new NullableLocalDateTimeDatabaseField(self, "ORIGINAL_EXPIRATION_DATE")
	}

	def primaryKey: IntDatabaseField = fields.assignId
}