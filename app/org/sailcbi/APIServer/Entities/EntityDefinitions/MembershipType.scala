package org.sailcbi.APIServer.Entities.EntityDefinitions

import org.sailcbi.APIServer.CbiUtil.InitializableFromCollectionElement
import org.sailcbi.APIServer.Storable.FieldValues.{IntFieldValue, NullableDoubleFieldValue, StringFieldValue}
import org.sailcbi.APIServer.Storable.Fields.{DoubleDatabaseField, IntDatabaseField, NullableDoubleDatabaseField, StringDatabaseField}
import org.sailcbi.APIServer.Storable._

class MembershipType extends StorableClass {
	val myself = this
	this.setCompanion(MembershipType)

	object references extends ReferencesObject {
		var program = new InitializableFromCollectionElement[ProgramType](_.values.programId.get == myself.values.programId.get)
	}

	object values extends ValuesObject {
		val membershipTypeId = new IntFieldValue(self, MembershipType.fields.membershipTypeId)
		val membershipTypeName = new StringFieldValue(self, MembershipType.fields.membershipTypeName)
		val programId = new IntFieldValue(self, MembershipType.fields.programId)
		val price = new NullableDoubleFieldValue(self, MembershipType.fields.price)
	}

	override val valuesList = List(
		values.membershipTypeId,
		values.membershipTypeName,
		values.programId,
		values.price
	)
}

object MembershipType extends StorableObject[MembershipType] {
	val entityName: String = "MEMBERSHIP_TYPES"

	object fields extends FieldsObject {
		val membershipTypeId = new IntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
		val membershipTypeName = new StringDatabaseField(self, "MEMBERSHIP_TYPE_NAME", 100)
		val programId = new IntDatabaseField(self, "PROGRAM_ID")
		val price = new NullableDoubleDatabaseField(self, "PRICE")
	}

	def primaryKey: IntDatabaseField = fields.membershipTypeId

	object specialIDs {
		val MEM_TYPE_ID_FULL_YEAR: Int = 1
		val MEM_TYPE_ID_30_DAY: Int = 4
		val MEM_TYPE_ID_60_DAY: Int = 5
		val MEM_TYPE_ID_1_DAY_MERC: Int = 6
		val MEM_TYPE_ID_1_DAY_KAYAK: Int = 7
		val MEM_TYPE_ID_LIFETIME: Int = 9
		val MEM_TYPE_ID_JUNIOR_SUMMER: Int = 10
		val MEM_TYPE_ID_UAP: Int = 13
		val MEM_TYPE_ID_HS_SPRING: Int = 14
		val MEM_TYPE_ID_HS_FALL: Int = 15
		val MEM_TYPE_ID_COMP_SEASONAL: Int = 61
		val MEM_TYPE_ID_1_DAY_SUP: Int = 521
		val MEM_TYPE_ID_1_DAY_RHODES: Int = 761
	}

}