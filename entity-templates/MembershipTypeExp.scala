package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class MembershipTypeExp extends StorableClass(MembershipTypeExp) {
	object values extends ValuesObject {
		val expirationId = new IntFieldValue(self, MembershipTypeExp.fields.expirationId)
		val membershipTypeId = new IntFieldValue(self, MembershipTypeExp.fields.membershipTypeId)
		val season = new DoubleFieldValue(self, MembershipTypeExp.fields.season)
		val expirationDate = new LocalDateTimeFieldValue(self, MembershipTypeExp.fields.expirationDate)
		val createdOn = new NullableLocalDateTimeFieldValue(self, MembershipTypeExp.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, MembershipTypeExp.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, MembershipTypeExp.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, MembershipTypeExp.fields.updatedBy)
		val startDate = new LocalDateTimeFieldValue(self, MembershipTypeExp.fields.startDate)
	}
}

object MembershipTypeExp extends StorableObject[MembershipTypeExp] {
	val entityName: String = "MEMBERSHIP_TYPE_EXP"

	object fields extends FieldsObject {
		val expirationId = new IntDatabaseField(self, "EXPIRATION_ID")
		val membershipTypeId = new IntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
		val season = new DoubleDatabaseField(self, "SEASON")
		val expirationDate = new LocalDateTimeDatabaseField(self, "EXPIRATION_DATE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val startDate = new LocalDateTimeDatabaseField(self, "START_DATE")
	}

	def primaryKey: IntDatabaseField = fields.expirationId
}