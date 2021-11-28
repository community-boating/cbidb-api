package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class SignupsWithAttendance extends StorableClass(SignupsWithAttendance) {
	object values extends ValuesObject {
		val personId = new IntFieldValue(self, SignupsWithAttendance.fields.personId)
		val typeId = new IntFieldValue(self, SignupsWithAttendance.fields.typeId)
		val typeName = new NullableStringFieldValue(self, SignupsWithAttendance.fields.typeName)
		val week = new NullableDoubleFieldValue(self, SignupsWithAttendance.fields.week)
		val instanceId = new IntFieldValue(self, SignupsWithAttendance.fields.instanceId)
		val signupId = new IntFieldValue(self, SignupsWithAttendance.fields.signupId)
		val ct = new NullableDoubleFieldValue(self, SignupsWithAttendance.fields.ct)
		val attendCt = new NullableDoubleFieldValue(self, SignupsWithAttendance.fields.attendCt)
	}
}

object SignupsWithAttendance extends StorableObject[SignupsWithAttendance] {
	val entityName: String = "SIGNUPS_WITH_ATTENDANCE"

	object fields extends FieldsObject {
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val typeName = new NullableStringDatabaseField(self, "TYPE_NAME", 100)
		val week = new NullableDoubleDatabaseField(self, "WEEK")
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val signupId = new IntDatabaseField(self, "SIGNUP_ID")
		val ct = new NullableDoubleDatabaseField(self, "CT")
		val attendCt = new NullableDoubleDatabaseField(self, "ATTEND_CT")
	}
}