package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class Signout extends StorableClass(Signout) {
	override object references extends ReferencesObject {
		val skipper = new Initializable[Person]
		val crew = new Initializable[List[SignoutCrew]]
		val tests = new Initializable[IndexedSeq[SignoutTest]]
	}

	override object values extends ValuesObject {
		val signoutId = new IntFieldValue(self, Signout.fields.signoutId)
		val programId = new IntFieldValue(self, Signout.fields.programId)
		val boatId = new IntFieldValue(self, Signout.fields.boatId)
		val cardNum = new NullableStringFieldValue(self, Signout.fields.cardNum)
		val sailNumber = new NullableStringFieldValue(self, Signout.fields.sailNumber)
		val signoutDatetime = new NullableDateTimeFieldValue(self, Signout.fields.signoutDatetime)
		val signinDatetime = new NullableDateTimeFieldValue(self, Signout.fields.signinDatetime)
		val testRatingId = new NullableIntFieldValue(self, Signout.fields.testRatingId)
		val testResult = new NullableStringFieldValue(self, Signout.fields.testResult)
		val signoutType = new StringFieldValue(self, Signout.fields.signoutType)
		val createdOn = new NullableDateTimeFieldValue(self, Signout.fields.createdOn)
		val createdBy = new StringFieldValue(self, Signout.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, Signout.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, Signout.fields.updatedBy)
		val didCapsize = new NullableBooleanFieldValue(self, Signout.fields.didCapsize)
		val isQueued = new BooleanFieldValue(self, Signout.fields.isQueued)
		val queueOrder = new NullableDoubleFieldValue(self, Signout.fields.queueOrder)
		val personId = new NullableIntFieldValue(self, Signout.fields.personId)
		val comments = new NullableStringFieldValue(self, Signout.fields.comments)
		val oldCrewCount = new NullableDoubleFieldValue(self, Signout.fields.oldCrewCount)
		val oldYouthCount = new NullableDoubleFieldValue(self, Signout.fields.oldYouthCount)
		val oldStudentCount = new NullableDoubleFieldValue(self, Signout.fields.oldStudentCount)
		val oldRunAground = new NullableBooleanFieldValue(self, Signout.fields.oldRunAground)
		val oldFirstName = new NullableStringFieldValue(self, Signout.fields.oldFirstName)
		val oldLastName = new NullableStringFieldValue(self, Signout.fields.oldLastName)
		val jpAttendanceId = new NullableIntFieldValue(self, Signout.fields.jpAttendanceId)
		val apAttendanceId = new NullableIntFieldValue(self, Signout.fields.apAttendanceId)
		val hullNumber = new NullableStringFieldValue(self, Signout.fields.hullNumber)
	}
}

object Signout extends StorableObject[Signout] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "SIGNOUTS"

	object fields extends FieldsObject {
		val signoutId = new IntDatabaseField(self, "SIGNOUT_ID")
		val programId = new IntDatabaseField(self, "PROGRAM_ID")
		val boatId = new IntDatabaseField(self, "BOAT_ID")
		val cardNum = new NullableStringDatabaseField(self, "CARD_NUM", 50)
		val sailNumber = new NullableStringDatabaseField(self, "SAIL_NUMBER", 15)
		val signoutDatetime = new NullableDateTimeDatabaseField(self, "SIGNOUT_DATETIME")
		val signinDatetime = new NullableDateTimeDatabaseField(self, "SIGNIN_DATETIME")
		val testRatingId = new NullableIntDatabaseField(self, "TEST_RATING_ID")
		val testResult = new NullableStringDatabaseField(self, "TEST_RESULT", 10)
		val signoutType = new StringDatabaseField(self, "SIGNOUT_TYPE", 1)
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val didCapsize = new NullableBooleanDatabaseField(self, "DID_CAPSIZE")
		val isQueued = new BooleanDatabaseField(self, "IS_QUEUED", false)
		val queueOrder = new NullableDoubleDatabaseField(self, "QUEUE_ORDER")
		val personId = new NullableIntDatabaseField(self, "PERSON_ID")
		val comments = new NullableStringDatabaseField(self, "COMMENTS", 4000)
		val oldCrewCount = new NullableDoubleDatabaseField(self, "OLD_CREW_COUNT")
		val oldYouthCount = new NullableDoubleDatabaseField(self, "OLD_YOUTH_COUNT")
		val oldStudentCount = new NullableDoubleDatabaseField(self, "OLD_STUDENT_COUNT")
		val oldRunAground = new NullableBooleanDatabaseField(self, "OLD_RUN_AGROUND")
		val oldFirstName = new NullableStringDatabaseField(self, "OLD_FIRST_NAME", 500)
		val oldLastName = new NullableStringDatabaseField(self, "OLD_LAST_NAME", 500)
		val jpAttendanceId = new NullableIntDatabaseField(self, "JP_ATTENDANCE_ID")
		val apAttendanceId = new NullableIntDatabaseField(self, "AP_ATTENDANCE_ID")
		val hullNumber = new NullableStringDatabaseField(self, "HULL_NUMBER", 15)
	}

	def primaryKey: IntDatabaseField = fields.signoutId
}