package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class SignoutCrew extends StorableClass(SignoutCrew) {
	override object references extends ReferencesObject {
		val person = new Initializable[Person]
	}

	override object values extends ValuesObject {
		val crewId = new IntFieldValue(self, SignoutCrew.fields.crewId)
		val cardNum = new NullableStringFieldValue(self, SignoutCrew.fields.cardNum)
		val signoutId = new IntFieldValue(self, SignoutCrew.fields.signoutId)
		val createdOn = new NullableDateTimeFieldValue(self, SignoutCrew.fields.createdOn)
		val createdBy = new StringFieldValue(self, SignoutCrew.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, SignoutCrew.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, SignoutCrew.fields.updatedBy)
		val personId = new NullableIntFieldValue(self, SignoutCrew.fields.personId)
		val startActive = new NullableDateTimeFieldValue(self, SignoutCrew.fields.startActive)
		val endActive = new NullableDateTimeFieldValue(self, SignoutCrew.fields.endActive)
		val oldFirstName = new NullableStringFieldValue(self, SignoutCrew.fields.oldFirstName)
		val oldLastName = new NullableStringFieldValue(self, SignoutCrew.fields.oldLastName)
		val jpAttendanceId = new NullableIntFieldValue(self, SignoutCrew.fields.jpAttendanceId)
		val apAttendanceId = new NullableIntFieldValue(self, SignoutCrew.fields.apAttendanceId)
	}
}

object SignoutCrew extends StorableObject[SignoutCrew] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "SIGNOUT_CREW"

	object fields extends FieldsObject {
		val crewId = new IntDatabaseField(self, "CREW_ID")
		val cardNum = new NullableStringDatabaseField(self, "CARD_NUM", 50)
		val signoutId = new IntDatabaseField(self, "SIGNOUT_ID")
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val personId = new NullableIntDatabaseField(self, "PERSON_ID")
		val startActive = new NullableDateTimeDatabaseField(self, "START_ACTIVE")
		val endActive = new NullableDateTimeDatabaseField(self, "END_ACTIVE")
		val oldFirstName = new NullableStringDatabaseField(self, "OLD_FIRST_NAME", 100)
		val oldLastName = new NullableStringDatabaseField(self, "OLD_LAST_NAME", 100)
		val jpAttendanceId = new NullableIntDatabaseField(self, "JP_ATTENDANCE_ID")
		val apAttendanceId = new NullableIntDatabaseField(self, "AP_ATTENDANCE_ID")
	}

	def primaryKey: IntDatabaseField = fields.crewId
}