package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class MembershipPrice extends StorableClass(MembershipPrice) {
	override object values extends ValuesObject {
		val instanceId = new IntFieldValue(self, MembershipPrice.fields.instanceId)
		val membershipTypeId = new IntFieldValue(self, MembershipPrice.fields.membershipTypeId)
		val price = new DoubleFieldValue(self, MembershipPrice.fields.price)
		val startActive = new NullableDateTimeFieldValue(self, MembershipPrice.fields.startActive)
		val endActive = new NullableDateTimeFieldValue(self, MembershipPrice.fields.endActive)
		val createdOn = new NullableDateTimeFieldValue(self, MembershipPrice.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, MembershipPrice.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, MembershipPrice.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, MembershipPrice.fields.updatedBy)
	}
}

object MembershipPrice extends StorableObject[MembershipPrice] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "MEMBERSHIP_PRICES"

	object fields extends FieldsObject {
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		@NullableInDatabase
		val membershipTypeId = new IntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
		@NullableInDatabase
		val price = new DoubleDatabaseField(self, "PRICE")
		val startActive = new NullableDateTimeDatabaseField(self, "START_ACTIVE")
		val endActive = new NullableDateTimeDatabaseField(self, "END_ACTIVE")
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.instanceId
}