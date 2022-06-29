package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class SignoutCrew extends StorableClass(SignoutCrew) {
	override object references extends ReferencesObject {
		val person = new Initializable[Person]
	}

	object values extends ValuesObject {
		val crewId = new IntFieldValue(self, SignoutCrew.fields.crewId)
		val cardNum = new NullableStringFieldValue(self, SignoutCrew.fields.cardNum)
		val signoutId = new IntFieldValue(self, SignoutCrew.fields.signoutId)
		val personId = new NullableIntFieldValue(self, SignoutCrew.fields.personId)
		val startActive = new NullableDateTimeFieldValue(self, SignoutCrew.fields.startActive)
		val endActive = new NullableDateTimeFieldValue(self, SignoutCrew.fields.endActive)
		val jpAttendanceId = new NullableIntFieldValue(self, SignoutCrew.fields.jpAttendanceId)
		val apAttendanceId = new NullableIntFieldValue(self, SignoutCrew.fields.apAttendanceId)
	}
}

object SignoutCrew extends StorableObject[SignoutCrew] {
	override val useRuntimeFieldnamesForJson: Boolean = true
	val entityName: String = "SIGNOUT_CREW"

	object fields extends FieldsObject {
		val crewId = new IntDatabaseField(self, "CREW_ID")
		val cardNum = new NullableStringDatabaseField(self, "CARD_NUM", 50)
		val signoutId = new IntDatabaseField(self, "SIGNOUT_ID")
		val personId = new NullableIntDatabaseField(self, "PERSON_ID")
		val startActive = new NullableDateTimeDatabaseField(self, "START_ACTIVE")
		val endActive = new NullableDateTimeDatabaseField(self, "END_ACTIVE")
		val jpAttendanceId = new NullableIntDatabaseField(self, "JP_ATTENDANCE_ID")
		val apAttendanceId = new NullableIntDatabaseField(self, "AP_ATTENDANCE_ID")
	}

	def primaryKey: IntDatabaseField = fields.crewId
}