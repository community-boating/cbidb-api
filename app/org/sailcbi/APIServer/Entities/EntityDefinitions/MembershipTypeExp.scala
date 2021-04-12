package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.framework.Storable.FieldValues.{DateFieldValue, IntFieldValue}
import com.coleji.framework.Storable.Fields.{DateDatabaseField, IntDatabaseField}
import com.coleji.framework.Storable._
import com.coleji.framework.Util.InitializableFromCollectionElement

class MembershipTypeExp extends StorableClass(MembershipTypeExp) {
	override object references extends ReferencesObject {
		val membershipType = new InitializableFromCollectionElement[MembershipType](_.values.membershipTypeId.get == values.membershipTypeId.get)
	}

	object values extends ValuesObject {
		val expirationId = new IntFieldValue(self, MembershipTypeExp.fields.expirationId)
		val membershipTypeId = new IntFieldValue(self, MembershipTypeExp.fields.membershipTypeId)
		val season = new IntFieldValue(self, MembershipTypeExp.fields.season)
		val startDate = new DateFieldValue(self, MembershipTypeExp.fields.startDate)
		val expirationDate = new DateFieldValue(self, MembershipTypeExp.fields.expirationDate)
	}
}

object MembershipTypeExp extends StorableObject[MembershipTypeExp] {
	val entityName: String = "MEMBERSHIP_TYPE_EXP"

	object fields extends FieldsObject {
		val expirationId = new IntDatabaseField(self, "EXPIRATION_ID")
		val membershipTypeId = new IntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
		val season = new IntDatabaseField(self, "SEASON")
		val startDate = new DateDatabaseField(self, "START_DATE")
		val expirationDate = new DateDatabaseField(self, "EXPIRATION_DATE")
	}

	def primaryKey: IntDatabaseField = fields.expirationId

}