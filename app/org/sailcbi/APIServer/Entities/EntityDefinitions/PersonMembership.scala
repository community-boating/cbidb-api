package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PersonMembership extends StorableClass(PersonMembership) {
	override object references extends ReferencesObject {
		val person = new Initializable[Person]
		val membershipType = new Initializable[MembershipType]
		val discountInstance = new Initializable[Option[DiscountInstance]]
		val guestPriv = new Initializable[Option[GuestPriv]]
	}

	override object values extends ValuesObject {
		val assignId = new IntFieldValue(self, PersonMembership.fields.assignId)
		val personId = new IntFieldValue(self, PersonMembership.fields.personId)
		val membershipTypeId = new IntFieldValue(self, PersonMembership.fields.membershipTypeId)
		val purchaseDate = new NullableDateTimeFieldValue(self, PersonMembership.fields.purchaseDate)
		val startDate = new NullableDateFieldValue(self, PersonMembership.fields.startDate)
		val expirationDate = new NullableDateFieldValue(self, PersonMembership.fields.expirationDate)
		val schoolId = new NullableIntFieldValue(self, PersonMembership.fields.schoolId)
		val orderId = new NullableIntFieldValue(self, PersonMembership.fields.orderId)
		val price = new DoubleFieldValue(self, PersonMembership.fields.price)
		val createdOn = new DateTimeFieldValue(self, PersonMembership.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, PersonMembership.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, PersonMembership.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, PersonMembership.fields.updatedBy)
		val temp = new BooleanFieldValue(self, PersonMembership.fields.temp)
		val paymentLocation = new NullableStringFieldValue(self, PersonMembership.fields.paymentLocation)
		val paymentMedium = new NullableStringFieldValue(self, PersonMembership.fields.paymentMedium)
		val discountId = new NullableIntFieldValue(self, PersonMembership.fields.discountId)
		val oldCardNum = new NullableStringFieldValue(self, PersonMembership.fields.oldCardNum)
		val oldCompReason = new NullableStringFieldValue(self, PersonMembership.fields.oldCompReason)
		val oldDmgWaiver = new NullableBooleanFieldValue(self, PersonMembership.fields.oldDmgWaiver)
		val closeId = new NullableIntFieldValue(self, PersonMembership.fields.closeId)
		val ccTransNum = new NullableDoubleFieldValue(self, PersonMembership.fields.ccTransNum)
		val groupId = new NullableIntFieldValue(self, PersonMembership.fields.groupId)
		val discountInstanceId = new NullableIntFieldValue(self, PersonMembership.fields.discountInstanceId)
		val notes = new NullableStringFieldValue(self, PersonMembership.fields.notes)
		val voidCloseId = new NullableIntFieldValue(self, PersonMembership.fields.voidCloseId)
		val discountAmt = new NullableDoubleFieldValue(self, PersonMembership.fields.discountAmt)
		val pretaxPrice = new NullableDoubleFieldValue(self, PersonMembership.fields.pretaxPrice)
		val taxRate = new NullableDoubleFieldValue(self, PersonMembership.fields.taxRate)
		val uapGroupId = new NullableIntFieldValue(self, PersonMembership.fields.uapGroupId)
		val ignoreDiscountFrozen = new NullableBooleanFieldValue(self, PersonMembership.fields.ignoreDiscountFrozen)
		val originalExpirationDate = new NullableDateTimeFieldValue(self, PersonMembership.fields.originalExpirationDate)
		val covidEmailSent = new NullableBooleanFieldValue(self, PersonMembership.fields.covidEmailSent)
		val covidDays = new NullableDoubleFieldValue(self, PersonMembership.fields.covidDays)
	}
}

object PersonMembership extends StorableObject[PersonMembership] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "PERSONS_MEMBERSHIPS"

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
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val temp = new BooleanDatabaseField(self, "TEMP", false)
		val paymentLocation = new NullableStringDatabaseField(self, "PAYMENT_LOCATION", 50)
		val paymentMedium = new NullableStringDatabaseField(self, "PAYMENT_MEDIUM", 50)
		val discountId = new NullableIntDatabaseField(self, "DISCOUNT_ID")
		val oldCardNum = new NullableStringDatabaseField(self, "OLD_CARD_NUM", 100)
		val oldCompReason = new NullableStringDatabaseField(self, "OLD_COMP_REASON", 500)
		val oldDmgWaiver = new NullableBooleanDatabaseField(self, "OLD_DMG_WAIVER")
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val ccTransNum = new NullableDoubleDatabaseField(self, "CC_TRANS_NUM")
		val groupId = new NullableIntDatabaseField(self, "GROUP_ID")
		val discountInstanceId = new NullableIntDatabaseField(self, "DISCOUNT_INSTANCE_ID")
		val notes = new NullableStringDatabaseField(self, "NOTES", 4000)
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
		val discountAmt = new NullableDoubleDatabaseField(self, "DISCOUNT_AMT")
		val pretaxPrice = new NullableDoubleDatabaseField(self, "PRETAX_PRICE")
		val taxRate = new NullableDoubleDatabaseField(self, "TAX_RATE")
		val uapGroupId = new NullableIntDatabaseField(self, "UAP_GROUP_ID")
		val ignoreDiscountFrozen = new NullableBooleanDatabaseField(self, "IGNORE_DISCOUNT_FROZEN")
		val originalExpirationDate = new NullableDateTimeDatabaseField(self, "ORIGINAL_EXPIRATION_DATE")
		val covidEmailSent = new NullableBooleanDatabaseField(self, "COVID_EMAIL_SENT")
		val covidDays = new NullableDoubleDatabaseField(self, "COVID_DAYS")
	}

	def primaryKey: IntDatabaseField = fields.assignId
}