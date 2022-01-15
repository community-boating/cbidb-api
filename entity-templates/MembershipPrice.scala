package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class MembershipPrice extends StorableClass(MembershipPrice) {
	object values extends ValuesObject {
		val instanceId = new IntFieldValue(self, MembershipPrice.fields.instanceId)
		val membershipTypeId = new NullableIntFieldValue(self, MembershipPrice.fields.membershipTypeId)
		val price = new NullableDoubleFieldValue(self, MembershipPrice.fields.price)
		val startActive = new NullableLocalDateTimeFieldValue(self, MembershipPrice.fields.startActive)
		val endActive = new NullableLocalDateTimeFieldValue(self, MembershipPrice.fields.endActive)
		val createdOn = new NullableLocalDateTimeFieldValue(self, MembershipPrice.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, MembershipPrice.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, MembershipPrice.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, MembershipPrice.fields.updatedBy)
	}
}

object MembershipPrice extends StorableObject[MembershipPrice] {
	val entityName: String = "MEMBERSHIP_PRICES"

	object fields extends FieldsObject {
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val membershipTypeId = new NullableIntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
		val price = new NullableDoubleDatabaseField(self, "PRICE")
		val startActive = new NullableLocalDateTimeDatabaseField(self, "START_ACTIVE")
		val endActive = new NullableLocalDateTimeDatabaseField(self, "END_ACTIVE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.instanceId
}