package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class MembershipTypeExp extends StorableClass(MembershipTypeExp) {
	override object references extends ReferencesObject {
		val membershipType = new Initializable[MembershipType]
	}

	override object values extends ValuesObject {
		val expirationId = new IntFieldValue(self, MembershipTypeExp.fields.expirationId)
		val membershipTypeId = new IntFieldValue(self, MembershipTypeExp.fields.membershipTypeId)
		val season = new IntFieldValue(self, MembershipTypeExp.fields.season)
		val expirationDate = new DateTimeFieldValue(self, MembershipTypeExp.fields.expirationDate)
		val createdOn = new DateTimeFieldValue(self, MembershipTypeExp.fields.createdOn)
		val createdBy = new StringFieldValue(self, MembershipTypeExp.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, MembershipTypeExp.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, MembershipTypeExp.fields.updatedBy)
		val startDate = new DateTimeFieldValue(self, MembershipTypeExp.fields.startDate)
	}
}

object MembershipTypeExp extends StorableObject[MembershipTypeExp] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "MEMBERSHIP_TYPE_EXP"

	object fields extends FieldsObject {
		val expirationId = new IntDatabaseField(self, "EXPIRATION_ID")
		val membershipTypeId = new IntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
		val season = new IntDatabaseField(self, "SEASON")
		val expirationDate = new DateTimeDatabaseField(self, "EXPIRATION_DATE")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val startDate = new DateTimeDatabaseField(self, "START_DATE")
	}

	def primaryKey: IntDatabaseField = fields.expirationId
}