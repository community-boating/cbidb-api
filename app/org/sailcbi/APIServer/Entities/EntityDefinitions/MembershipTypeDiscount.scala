package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class MembershipTypeDiscount extends StorableClass(MembershipTypeDiscount) {
	override object values extends ValuesObject {
		val discountId = new IntFieldValue(self, MembershipTypeDiscount.fields.discountId)
		val membershipTypeId = new NullableIntFieldValue(self, MembershipTypeDiscount.fields.membershipTypeId)
		val discountAmt = new NullableDoubleFieldValue(self, MembershipTypeDiscount.fields.discountAmt)
		val description = new StringFieldValue(self, MembershipTypeDiscount.fields.description)
		val createdOn = new DateTimeFieldValue(self, MembershipTypeDiscount.fields.createdOn)
		val createdBy = new StringFieldValue(self, MembershipTypeDiscount.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, MembershipTypeDiscount.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, MembershipTypeDiscount.fields.updatedBy)
		val regCodeRowId = new NullableIntFieldValue(self, MembershipTypeDiscount.fields.regCodeRowId)
	}
}

object MembershipTypeDiscount extends StorableObject[MembershipTypeDiscount] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "MEMBERSHIP_TYPE_DISCOUNTS"

	object fields extends FieldsObject {
		val discountId = new IntDatabaseField(self, "DISCOUNT_ID")
		val membershipTypeId = new NullableIntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
		val discountAmt = new NullableDoubleDatabaseField(self, "DISCOUNT_AMT")
		@NullableInDatabase
		val description = new StringDatabaseField(self, "DESCRIPTION", 100)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val regCodeRowId = new NullableIntDatabaseField(self, "REG_CODE_ROW_ID")
	}

	def primaryKey: IntDatabaseField = fields.discountId
}