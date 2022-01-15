package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class TodaysSignout extends StorableClass(TodaysSignout) {
	object values extends ValuesObject {
		val signoutId = new IntFieldValue(self, TodaysSignout.fields.signoutId)
		val programId = new IntFieldValue(self, TodaysSignout.fields.programId)
		val boatId = new IntFieldValue(self, TodaysSignout.fields.boatId)
		val cardNum = new NullableStringFieldValue(self, TodaysSignout.fields.cardNum)
		val sailNumber = new NullableStringFieldValue(self, TodaysSignout.fields.sailNumber)
		val signoutDatetime = new NullableLocalDateTimeFieldValue(self, TodaysSignout.fields.signoutDatetime)
		val signinDatetime = new NullableLocalDateTimeFieldValue(self, TodaysSignout.fields.signinDatetime)
		val testRatingId = new NullableIntFieldValue(self, TodaysSignout.fields.testRatingId)
		val testResult = new NullableStringFieldValue(self, TodaysSignout.fields.testResult)
		val signoutType = new BooleanFIeldValue(self, TodaysSignout.fields.signoutType)
		val createdOn = new NullableLocalDateTimeFieldValue(self, TodaysSignout.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, TodaysSignout.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, TodaysSignout.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, TodaysSignout.fields.updatedBy)
		val didCapsize = new NullableBooleanFIeldValue(self, TodaysSignout.fields.didCapsize)
		val isQueued = new BooleanFIeldValue(self, TodaysSignout.fields.isQueued)
		val queueOrder = new NullableDoubleFieldValue(self, TodaysSignout.fields.queueOrder)
		val personId = new NullableIntFieldValue(self, TodaysSignout.fields.personId)
		val comments = new NullableStringFieldValue(self, TodaysSignout.fields.comments)
		val oldCrewCount = new NullableDoubleFieldValue(self, TodaysSignout.fields.oldCrewCount)
		val oldYouthCount = new NullableDoubleFieldValue(self, TodaysSignout.fields.oldYouthCount)
		val oldStudentCount = new NullableDoubleFieldValue(self, TodaysSignout.fields.oldStudentCount)
		val oldRunAground = new NullableBooleanFIeldValue(self, TodaysSignout.fields.oldRunAground)
		val oldFirstName = new NullableStringFieldValue(self, TodaysSignout.fields.oldFirstName)
		val oldLastName = new NullableStringFieldValue(self, TodaysSignout.fields.oldLastName)
		val jpAttendanceId = new NullableIntFieldValue(self, TodaysSignout.fields.jpAttendanceId)
		val hullNumber = new NullableStringFieldValue(self, TodaysSignout.fields.hullNumber)
	}
}

object TodaysSignout extends StorableObject[TodaysSignout] {
	val entityName: String = "TODAYS_SIGNOUTS"

	object fields extends FieldsObject {
		val signoutId = new IntDatabaseField(self, "SIGNOUT_ID")
		val programId = new IntDatabaseField(self, "PROGRAM_ID")
		val boatId = new IntDatabaseField(self, "BOAT_ID")
		val cardNum = new NullableStringDatabaseField(self, "CARD_NUM", 50)
		val sailNumber = new NullableStringDatabaseField(self, "SAIL_NUMBER", 15)
		val signoutDatetime = new NullableLocalDateTimeDatabaseField(self, "SIGNOUT_DATETIME")
		val signinDatetime = new NullableLocalDateTimeDatabaseField(self, "SIGNIN_DATETIME")
		val testRatingId = new NullableIntDatabaseField(self, "TEST_RATING_ID")
		val testResult = new NullableStringDatabaseField(self, "TEST_RESULT", 10)
		val signoutType = new BooleanDatabaseField(self, "SIGNOUT_TYPE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val didCapsize = new NullableBooleanDatabaseField(self, "DID_CAPSIZE")
		val isQueued = new BooleanDatabaseField(self, "IS_QUEUED")
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
		val hullNumber = new NullableStringDatabaseField(self, "HULL_NUMBER", 15)
	}
}