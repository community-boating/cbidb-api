package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PersonMembership extends StorableClass(PersonMembership) {
	override object references extends ReferencesObject {
		val person: Option[Person] = None
		val membershipType = new Initializable[MembershipType]
	}

	object values extends ValuesObject {
		val assignId = new IntFieldValue(self, PersonMembership.fields.assignId)
		val personId = new IntFieldValue(self, PersonMembership.fields.personId)
		val membershipTypeId = new IntFieldValue(self, PersonMembership.fields.membershipTypeId)
		val purchaseDate = new NullableDateTimeFieldValue(self, PersonMembership.fields.purchaseDate)
		val startDate = new NullableDateFieldValue(self, PersonMembership.fields.startDate)
		val expirationDate = new NullableDateFieldValue(self, PersonMembership.fields.expirationDate)
		val schoolId = new NullableIntFieldValue(self, PersonMembership.fields.schoolId)
		val orderId = new NullableIntFieldValue(self, PersonMembership.fields.orderId)
		val price = new DoubleFieldValue(self, PersonMembership.fields.price)
//		val createdOn = new NullableLocalDateTimeFieldValue(self, PersonMembership.fields.createdOn)
//		val createdBy = new NullableStringFieldValue(self, PersonMembership.fields.createdBy)
//		val updatedOn = new NullableLocalDateTimeFieldValue(self, PersonMembership.fields.updatedOn)
//		val updatedBy = new NullableStringFieldValue(self, PersonMembership.fields.updatedBy)
		val temp = new BooleanFieldValue(self, PersonMembership.fields.temp)
		val paymentLocation = new NullableStringFieldValue(self, PersonMembership.fields.paymentLocation)
		val paymentMedium = new NullableStringFieldValue(self, PersonMembership.fields.paymentMedium)
//		val oldCardNum = new NullableStringFieldValue(self, PersonMembership.fields.oldCardNum)
//		val oldCompReason = new NullableStringFieldValue(self, PersonMembership.fields.oldCompReason)
//		val oldDmgWaiver = new NullableBooleanFIeldValue(self, PersonMembership.fields.oldDmgWaiver)
		val closeId = new NullableIntFieldValue(self, PersonMembership.fields.closeId)
		val ccTransNum = new NullableIntFieldValue(self, PersonMembership.fields.ccTransNum)
		val groupId = new NullableIntFieldValue(self, PersonMembership.fields.groupId)
		val discountInstanceId = new NullableIntFieldValue(self, PersonMembership.fields.discountInstanceId)
		val notes = new NullableStringFieldValue(self, PersonMembership.fields.notes)
//		val discountId = new NullableIntFieldValue(self, PersonMembership.fields.discountId)
		val voidCloseId = new NullableIntFieldValue(self, PersonMembership.fields.voidCloseId)
		val discountAmt = new NullableDoubleFieldValue(self, PersonMembership.fields.discountAmt)
		val taxRate = new NullableDoubleFieldValue(self, PersonMembership.fields.taxRate)
		val pretaxPrice = new NullableDoubleFieldValue(self, PersonMembership.fields.pretaxPrice)
		val uapGroupId = new NullableIntFieldValue(self, PersonMembership.fields.uapGroupId)
		val ignoreDiscountFrozen = new BooleanFieldValue(self, PersonMembership.fields.ignoreDiscountFrozen)
//		val covidDays = new NullableDoubleFieldValue(self, PersonMembership.fields.covidDays)
//		val covidEmailSent = new NullableBooleanFIeldValue(self, PersonMembership.fields.covidEmailSent)
//		val originalExpirationDate = new NullableLocalDateTimeFieldValue(self, PersonMembership.fields.originalExpirationDate)
	}
}

object PersonMembership extends StorableObject[PersonMembership] {
	val entityName: String = "PERSONS_MEMBERSHIPS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val membershipTypeId = new IntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
		val purchaseDate = new NullableDateTimeDatabaseField(self, "PURCHASE_DATE")
		val startDate = new NullableDateDatabaseField(self, "START_DATE")
		val expirationDate = new NullableDateDatabaseField(self, "EXPIRATION_DATE")
		val schoolId = new NullableIntDatabaseField(self, "SCHOOL_ID")
		val orderId = new NullableIntDatabaseField(self, "ORDER_ID")
		val price = new DoubleDatabaseField(self, "PRICE")
//		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
//		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
//		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
//		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val temp = new BooleanDatabaseField(self, "TEMP")
		val paymentLocation = new NullableStringDatabaseField(self, "PAYMENT_LOCATION", 50)
		val paymentMedium = new NullableStringDatabaseField(self, "PAYMENT_MEDIUM", 50)
//		val oldCardNum = new NullableStringDatabaseField(self, "OLD_CARD_NUM", 100)
//		val oldCompReason = new NullableStringDatabaseField(self, "OLD_COMP_REASON", 500)
//		val oldDmgWaiver = new NullableBooleanDatabaseField(self, "OLD_DMG_WAIVER")
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val ccTransNum = new NullableIntDatabaseField(self, "CC_TRANS_NUM")
		val groupId = new NullableIntDatabaseField(self, "GROUP_ID")
		val discountInstanceId = new NullableIntDatabaseField(self, "DISCOUNT_INSTANCE_ID")
		val notes = new NullableStringDatabaseField(self, "NOTES", 4000)
//		val discountId = new NullableIntDatabaseField(self, "DISCOUNT_ID")
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
		val discountAmt = new NullableDoubleDatabaseField(self, "DISCOUNT_AMT")
		val taxRate = new NullableDoubleDatabaseField(self, "TAX_RATE")
		val pretaxPrice = new NullableDoubleDatabaseField(self, "PRETAX_PRICE")
		val uapGroupId = new NullableIntDatabaseField(self, "UAP_GROUP_ID")
		val ignoreDiscountFrozen = new BooleanDatabaseField(self, "IGNORE_DISCOUNT_FROZEN", true)
//		val covidDays = new NullableDoubleDatabaseField(self, "COVID_DAYS")
//		val covidEmailSent = new NullableBooleanDatabaseField(self, "COVID_EMAIL_SENT")
//		val originalExpirationDate = new NullableLocalDateTimeDatabaseField(self, "ORIGINAL_EXPIRATION_DATE")
	}

	def primaryKey: IntDatabaseField = fields.assignId
}
