package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.framework.Storable.FieldValues.{IntFieldValue, NullableDoubleFieldValue, StringFieldValue}
import com.coleji.framework.Storable.Fields.{IntDatabaseField, NullableDoubleDatabaseField, StringDatabaseField}
import com.coleji.framework.Storable._
import com.coleji.framework.Util.InitializableFromCollectionElement
import org.sailcbi.APIServer.Entities.NullableInDatabase

class MembershipType extends StorableClass(MembershipType) {
	override object references extends ReferencesObject {
		val program = new InitializableFromCollectionElement[ProgramType](_.values.programId.get == values.programId.get)
	}

	object values extends ValuesObject {
		val membershipTypeId = new IntFieldValue(self, MembershipType.fields.membershipTypeId)
		val membershipTypeName = new StringFieldValue(self, MembershipType.fields.membershipTypeName)
		val programId = new IntFieldValue(self, MembershipType.fields.programId)
		val price = new NullableDoubleFieldValue(self, MembershipType.fields.price)
	}
}

object MembershipType extends StorableObject[MembershipType] {
	val entityName: String = "MEMBERSHIP_TYPES"

	object fields extends FieldsObject {
		val membershipTypeId = new IntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
		@NullableInDatabase
		val membershipTypeName = new StringDatabaseField(self, "MEMBERSHIP_TYPE_NAME", 100)
		@NullableInDatabase
		val programId = new IntDatabaseField(self, "PROGRAM_ID")
		val price = new NullableDoubleDatabaseField(self, "PRICE")
	}

	def primaryKey: IntDatabaseField = fields.membershipTypeId
}