package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ApCurrentMembership extends StorableClass(ApCurrentMembership) {
	object values extends ValuesObject {
		val personId = new IntFieldValue(self, ApCurrentMembership.fields.personId)
		val membershipTypeName = new NullableStringFieldValue(self, ApCurrentMembership.fields.membershipTypeName)
		val programPriority = new NullableDoubleFieldValue(self, ApCurrentMembership.fields.programPriority)
		val assignId = new IntFieldValue(self, ApCurrentMembership.fields.assignId)
	}
}

object ApCurrentMembership extends StorableObject[ApCurrentMembership] {
	val entityName: String = "AP_CURRENT_MEMBERSHIPS"

	object fields extends FieldsObject {
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val membershipTypeName = new NullableStringDatabaseField(self, "MEMBERSHIP_TYPE_NAME", 200)
		val programPriority = new NullableDoubleDatabaseField(self, "PROGRAM_PRIORITY")
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
	}
}