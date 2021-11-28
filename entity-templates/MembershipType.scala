package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class MembershipType extends StorableClass(MembershipType) {
	object values extends ValuesObject {
		val membershipTypeId = new IntFieldValue(self, MembershipType.fields.membershipTypeId)
		val programId = new NullableIntFieldValue(self, MembershipType.fields.programId)
		val membershipTypeName = new NullableStringFieldValue(self, MembershipType.fields.membershipTypeName)
		val displayOrder = new NullableDoubleFieldValue(self, MembershipType.fields.displayOrder)
		val active = new NullableBooleanFIeldValue(self, MembershipType.fields.active)
		val createdOn = new NullableLocalDateTimeFieldValue(self, MembershipType.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, MembershipType.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, MembershipType.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, MembershipType.fields.updatedBy)
		val duration = new NullableDoubleFieldValue(self, MembershipType.fields.duration)
		val expirationType = new NullableStringFieldValue(self, MembershipType.fields.expirationType)
		val price = new NullableDoubleFieldValue(self, MembershipType.fields.price)
		val oldDiscountType = new NullableDoubleFieldValue(self, MembershipType.fields.oldDiscountType)
		val regCodeRowId = new NullableIntFieldValue(self, MembershipType.fields.regCodeRowId)
		val alertOnClose = new NullableBooleanFIeldValue(self, MembershipType.fields.alertOnClose)
		val gcEligible = new NullableBooleanFIeldValue(self, MembershipType.fields.gcEligible)
		val membershipTypeDisplayName = new NullableStringFieldValue(self, MembershipType.fields.membershipTypeDisplayName)
		val availableOnline = new NullableBooleanFIeldValue(self, MembershipType.fields.availableOnline)
		val canHaveGuestPrivs = new NullableBooleanFIeldValue(self, MembershipType.fields.canHaveGuestPrivs)
		val gcRegCodeRowId = new NullableIntFieldValue(self, MembershipType.fields.gcRegCodeRowId)
		val ratingsRestricted = new NullableBooleanFIeldValue(self, MembershipType.fields.ratingsRestricted)
		val taxable = new NullableBooleanFIeldValue(self, MembershipType.fields.taxable)
		val programPriority = new NullableDoubleFieldValue(self, MembershipType.fields.programPriority)
		val renewalEligible = new NullableBooleanFIeldValue(self, MembershipType.fields.renewalEligible)
	}
}

object MembershipType extends StorableObject[MembershipType] {
	val entityName: String = "MEMBERSHIP_TYPES"

	object fields extends FieldsObject {
		val membershipTypeId = new IntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
		val programId = new NullableIntDatabaseField(self, "PROGRAM_ID")
		val membershipTypeName = new NullableStringDatabaseField(self, "MEMBERSHIP_TYPE_NAME", 200)
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val duration = new NullableDoubleDatabaseField(self, "DURATION")
		val expirationType = new NullableStringDatabaseField(self, "EXPIRATION_TYPE", 50)
		val price = new NullableDoubleDatabaseField(self, "PRICE")
		val oldDiscountType = new NullableDoubleDatabaseField(self, "OLD_DISCOUNT_TYPE")
		val regCodeRowId = new NullableIntDatabaseField(self, "REG_CODE_ROW_ID")
		val alertOnClose = new NullableBooleanDatabaseField(self, "ALERT_ON_CLOSE")
		val gcEligible = new NullableBooleanDatabaseField(self, "GC_ELIGIBLE")
		val membershipTypeDisplayName = new NullableStringDatabaseField(self, "MEMBERSHIP_TYPE_DISPLAY_NAME", 100)
		val availableOnline = new NullableBooleanDatabaseField(self, "AVAILABLE_ONLINE")
		val canHaveGuestPrivs = new NullableBooleanDatabaseField(self, "CAN_HAVE_GUEST_PRIVS")
		val gcRegCodeRowId = new NullableIntDatabaseField(self, "GC_REG_CODE_ROW_ID")
		val ratingsRestricted = new NullableBooleanDatabaseField(self, "RATINGS_RESTRICTED")
		val taxable = new NullableBooleanDatabaseField(self, "TAXABLE")
		val programPriority = new NullableDoubleDatabaseField(self, "PROGRAM_PRIORITY")
		val renewalEligible = new NullableBooleanDatabaseField(self, "RENEWAL_ELIGIBLE")
	}

	def primaryKey: IntDatabaseField = fields.membershipTypeId
}