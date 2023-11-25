package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class MembershipType extends StorableClass(MembershipType) {
	override object references extends ReferencesObject {
		val program = new Initializable[ProgramType]
	}

	override object values extends ValuesObject {
		val membershipTypeId = new IntFieldValue(self, MembershipType.fields.membershipTypeId)
		val programId = new IntFieldValue(self, MembershipType.fields.programId)
		val membershipTypeName = new StringFieldValue(self, MembershipType.fields.membershipTypeName)
		val displayOrder = new DoubleFieldValue(self, MembershipType.fields.displayOrder)
		val active = new NullableBooleanFieldValue(self, MembershipType.fields.active)
		val createdOn = new DateTimeFieldValue(self, MembershipType.fields.createdOn)
		val createdBy = new StringFieldValue(self, MembershipType.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, MembershipType.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, MembershipType.fields.updatedBy)
		val duration = new NullableIntFieldValue(self, MembershipType.fields.duration)
		val expirationType = new StringFieldValue(self, MembershipType.fields.expirationType)
		val price = new NullableDoubleFieldValue(self, MembershipType.fields.price)
		val oldDiscountType = new NullableDoubleFieldValue(self, MembershipType.fields.oldDiscountType)
		val regCodeRowId = new NullableIntFieldValue(self, MembershipType.fields.regCodeRowId)
		val alertOnClose = new NullableBooleanFieldValue(self, MembershipType.fields.alertOnClose)
		val gcEligible = new NullableBooleanFieldValue(self, MembershipType.fields.gcEligible)
		val membershipTypeDisplayName = new NullableStringFieldValue(self, MembershipType.fields.membershipTypeDisplayName)
		val availableOnline = new NullableBooleanFieldValue(self, MembershipType.fields.availableOnline)
		val canHaveGuestPrivs = new NullableBooleanFieldValue(self, MembershipType.fields.canHaveGuestPrivs)
		val gcRegCodeRowId = new NullableIntFieldValue(self, MembershipType.fields.gcRegCodeRowId)
		val ratingsRestricted = new NullableBooleanFieldValue(self, MembershipType.fields.ratingsRestricted)
		val taxable = new NullableBooleanFieldValue(self, MembershipType.fields.taxable)
		val programPriority = new NullableDoubleFieldValue(self, MembershipType.fields.programPriority)
		val renewalEligible = new NullableBooleanFieldValue(self, MembershipType.fields.renewalEligible)
	}
}

object MembershipType extends StorableObject[MembershipType] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "MEMBERSHIP_TYPES"

	object fields extends FieldsObject {
		val membershipTypeId = new IntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
		@NullableInDatabase
		val programId = new IntDatabaseField(self, "PROGRAM_ID")
		@NullableInDatabase
		val membershipTypeName = new StringDatabaseField(self, "MEMBERSHIP_TYPE_NAME", 200)
		val displayOrder = new DoubleDatabaseField(self, "DISPLAY_ORDER")
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val duration = new NullableIntDatabaseField(self, "DURATION")
		@NullableInDatabase
		val expirationType = new StringDatabaseField(self, "EXPIRATION_TYPE", 50)
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