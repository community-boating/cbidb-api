package org.sailcbi.APIServer.Entities.EntityDefinitions

import org.sailcbi.APIServer.CbiUtil.InitializableFromCollectionElement
import com.coleji.framework.Storable.FieldValues.{DateFieldValue, IntFieldValue}
import com.coleji.framework.Storable.Fields.{DateDatabaseField, IntDatabaseField}
import com.coleji.framework.Storable.{FieldsObject, ReferencesObject, StorableClass, StorableObject, ValuesObject}
import com.coleji.framework.Storable._

class MembershipTypeExp extends StorableClass {
	val myself = this
	this.setCompanion(MembershipTypeExp)

	object references extends ReferencesObject {
		var membershipType = new InitializableFromCollectionElement[MembershipType](_.values.membershipTypeId.get == myself.values.membershipTypeId.get)
	}

	object values extends ValuesObject {
		val expirationId = new IntFieldValue(self, MembershipTypeExp.fields.expirationId)
		val membershipTypeId = new IntFieldValue(self, MembershipTypeExp.fields.membershipTypeId)
		val season = new IntFieldValue(self, MembershipTypeExp.fields.season)
		val startDate = new DateFieldValue(self, MembershipTypeExp.fields.startDate)
		val expirationDate = new DateFieldValue(self, MembershipTypeExp.fields.expirationDate)
	}

	override val valuesList = List(
		values.expirationId,
		values.membershipTypeId,
		values.season,
		values.startDate,
		values.expirationDate
	)
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