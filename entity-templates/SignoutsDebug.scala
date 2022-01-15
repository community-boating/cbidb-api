package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class SignoutsDebug extends StorableClass(SignoutsDebug) {
	object values extends ValuesObject {
		val signoutId = new NullableIntFieldValue(self, SignoutsDebug.fields.signoutId)
		val programId = new IntFieldValue(self, SignoutsDebug.fields.programId)
		val boatId = new IntFieldValue(self, SignoutsDebug.fields.boatId)
		val cardNum = new NullableStringFieldValue(self, SignoutsDebug.fields.cardNum)
		val sailNumber = new NullableStringFieldValue(self, SignoutsDebug.fields.sailNumber)
		val signoutDatetime = new NullableLocalDateTimeFieldValue(self, SignoutsDebug.fields.signoutDatetime)
		val signinDatetime = new NullableLocalDateTimeFieldValue(self, SignoutsDebug.fields.signinDatetime)
		val testRatingId = new NullableIntFieldValue(self, SignoutsDebug.fields.testRatingId)
		val testResult = new NullableStringFieldValue(self, SignoutsDebug.fields.testResult)
		val signoutType = new BooleanFIeldValue(self, SignoutsDebug.fields.signoutType)
		val createdOn = new NullableLocalDateTimeFieldValue(self, SignoutsDebug.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, SignoutsDebug.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, SignoutsDebug.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, SignoutsDebug.fields.updatedBy)
		val didCapsize = new NullableBooleanFIeldValue(self, SignoutsDebug.fields.didCapsize)
		val isQueued = new BooleanFIeldValue(self, SignoutsDebug.fields.isQueued)
		val queueOrder = new NullableDoubleFieldValue(self, SignoutsDebug.fields.queueOrder)
		val personId = new NullableIntFieldValue(self, SignoutsDebug.fields.personId)
		val comments = new NullableStringFieldValue(self, SignoutsDebug.fields.comments)
		val oldCrewCount = new NullableDoubleFieldValue(self, SignoutsDebug.fields.oldCrewCount)
		val oldYouthCount = new NullableDoubleFieldValue(self, SignoutsDebug.fields.oldYouthCount)
		val oldStudentCount = new NullableDoubleFieldValue(self, SignoutsDebug.fields.oldStudentCount)
		val oldRunAground = new NullableBooleanFIeldValue(self, SignoutsDebug.fields.oldRunAground)
		val oldFirstName = new NullableStringFieldValue(self, SignoutsDebug.fields.oldFirstName)
		val oldLastName = new NullableStringFieldValue(self, SignoutsDebug.fields.oldLastName)
		val jpAttendanceId = new NullableIntFieldValue(self, SignoutsDebug.fields.jpAttendanceId)
		val apAttendanceId = new NullableIntFieldValue(self, SignoutsDebug.fields.apAttendanceId)
		val snapshotType = new NullableBooleanFIeldValue(self, SignoutsDebug.fields.snapshotType)
		val snapshotDatetime = new NullableLocalDateTimeFieldValue(self, SignoutsDebug.fields.snapshotDatetime)
		val snapshotId = new IntFieldValue(self, SignoutsDebug.fields.snapshotId)
	}
}

object SignoutsDebug extends StorableObject[SignoutsDebug] {
	val entityName: String = "SIGNOUTS_DEBUG"

	object fields extends FieldsObject {
		val signoutId = new NullableIntDatabaseField(self, "SIGNOUT_ID")
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
		val apAttendanceId = new NullableIntDatabaseField(self, "AP_ATTENDANCE_ID")
		val snapshotType = new NullableBooleanDatabaseField(self, "SNAPSHOT_TYPE")
		val snapshotDatetime = new NullableLocalDateTimeDatabaseField(self, "SNAPSHOT_DATETIME")
		val snapshotId = new IntDatabaseField(self, "SNAPSHOT_ID")
	}

	def primaryKey: IntDatabaseField = fields.snapshotId
}