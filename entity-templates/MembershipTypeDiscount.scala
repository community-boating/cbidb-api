package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class MembershipTypeDiscount extends StorableClass(MembershipTypeDiscount) {
	object values extends ValuesObject {
		val discountId = new IntFieldValue(self, MembershipTypeDiscount.fields.discountId)
		val membershipTypeId = new NullableIntFieldValue(self, MembershipTypeDiscount.fields.membershipTypeId)
		val discountAmt = new NullableDoubleFieldValue(self, MembershipTypeDiscount.fields.discountAmt)
		val description = new NullableStringFieldValue(self, MembershipTypeDiscount.fields.description)
		val createdOn = new NullableLocalDateTimeFieldValue(self, MembershipTypeDiscount.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, MembershipTypeDiscount.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, MembershipTypeDiscount.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, MembershipTypeDiscount.fields.updatedBy)
		val regCodeRowId = new NullableIntFieldValue(self, MembershipTypeDiscount.fields.regCodeRowId)
	}
}

object MembershipTypeDiscount extends StorableObject[MembershipTypeDiscount] {
	val entityName: String = "MEMBERSHIP_TYPE_DISCOUNTS"

	object fields extends FieldsObject {
		val discountId = new IntDatabaseField(self, "DISCOUNT_ID")
		val membershipTypeId = new NullableIntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
		val discountAmt = new NullableDoubleDatabaseField(self, "DISCOUNT_AMT")
		val description = new NullableStringDatabaseField(self, "DESCRIPTION", 100)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val regCodeRowId = new NullableIntDatabaseField(self, "REG_CODE_ROW_ID")
	}

	def primaryKey: IntDatabaseField = fields.discountId
}