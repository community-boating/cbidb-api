package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class SignoutCrew extends StorableClass(SignoutCrew) {
	object values extends ValuesObject {
		val crewId = new IntFieldValue(self, SignoutCrew.fields.crewId)
		val cardNum = new NullableStringFieldValue(self, SignoutCrew.fields.cardNum)
		val signoutId = new NullableIntFieldValue(self, SignoutCrew.fields.signoutId)
		val createdOn = new NullableLocalDateTimeFieldValue(self, SignoutCrew.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, SignoutCrew.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, SignoutCrew.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, SignoutCrew.fields.updatedBy)
		val personId = new NullableIntFieldValue(self, SignoutCrew.fields.personId)
		val startActive = new NullableLocalDateTimeFieldValue(self, SignoutCrew.fields.startActive)
		val endActive = new NullableLocalDateTimeFieldValue(self, SignoutCrew.fields.endActive)
		val oldFirstName = new NullableStringFieldValue(self, SignoutCrew.fields.oldFirstName)
		val oldLastName = new NullableStringFieldValue(self, SignoutCrew.fields.oldLastName)
		val jpAttendanceId = new NullableIntFieldValue(self, SignoutCrew.fields.jpAttendanceId)
		val apAttendanceId = new NullableIntFieldValue(self, SignoutCrew.fields.apAttendanceId)
	}
}

object SignoutCrew extends StorableObject[SignoutCrew] {
	val entityName: String = "SIGNOUT_CREW"

	object fields extends FieldsObject {
		val crewId = new IntDatabaseField(self, "CREW_ID")
		val cardNum = new NullableStringDatabaseField(self, "CARD_NUM", 50)
		val signoutId = new NullableIntDatabaseField(self, "SIGNOUT_ID")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val personId = new NullableIntDatabaseField(self, "PERSON_ID")
		val startActive = new NullableLocalDateTimeDatabaseField(self, "START_ACTIVE")
		val endActive = new NullableLocalDateTimeDatabaseField(self, "END_ACTIVE")
		val oldFirstName = new NullableStringDatabaseField(self, "OLD_FIRST_NAME", 100)
		val oldLastName = new NullableStringDatabaseField(self, "OLD_LAST_NAME", 100)
		val jpAttendanceId = new NullableIntDatabaseField(self, "JP_ATTENDANCE_ID")
		val apAttendanceId = new NullableIntDatabaseField(self, "AP_ATTENDANCE_ID")
	}

	def primaryKey: IntDatabaseField = fields.crewId
}