package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class SignoutsDebug extends StorableClass(SignoutsDebug) {
	override object values extends ValuesObject {
		val signoutId = new NullableIntFieldValue(self, SignoutsDebug.fields.signoutId)
		val programId = new IntFieldValue(self, SignoutsDebug.fields.programId)
		val boatId = new IntFieldValue(self, SignoutsDebug.fields.boatId)
		val cardNum = new NullableStringFieldValue(self, SignoutsDebug.fields.cardNum)
		val sailNumber = new NullableStringFieldValue(self, SignoutsDebug.fields.sailNumber)
		val signoutDatetime = new NullableDateTimeFieldValue(self, SignoutsDebug.fields.signoutDatetime)
		val signinDatetime = new NullableDateTimeFieldValue(self, SignoutsDebug.fields.signinDatetime)
		val testRatingId = new NullableIntFieldValue(self, SignoutsDebug.fields.testRatingId)
		val testResult = new NullableStringFieldValue(self, SignoutsDebug.fields.testResult)
		val signoutType = new StringFieldValue(self, SignoutsDebug.fields.signoutType)
		val createdOn = new DateTimeFieldValue(self, SignoutsDebug.fields.createdOn)
		val createdBy = new StringFieldValue(self, SignoutsDebug.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, SignoutsDebug.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, SignoutsDebug.fields.updatedBy)
		val didCapsize = new NullableBooleanFieldValue(self, SignoutsDebug.fields.didCapsize)
		val isQueued = new BooleanFieldValue(self, SignoutsDebug.fields.isQueued)
		val queueOrder = new NullableDoubleFieldValue(self, SignoutsDebug.fields.queueOrder)
		val personId = new NullableIntFieldValue(self, SignoutsDebug.fields.personId)
		val comments = new NullableStringFieldValue(self, SignoutsDebug.fields.comments)
		val oldCrewCount = new NullableDoubleFieldValue(self, SignoutsDebug.fields.oldCrewCount)
		val oldYouthCount = new NullableDoubleFieldValue(self, SignoutsDebug.fields.oldYouthCount)
		val oldStudentCount = new NullableDoubleFieldValue(self, SignoutsDebug.fields.oldStudentCount)
		val oldRunAground = new NullableBooleanFieldValue(self, SignoutsDebug.fields.oldRunAground)
		val oldFirstName = new NullableStringFieldValue(self, SignoutsDebug.fields.oldFirstName)
		val oldLastName = new NullableStringFieldValue(self, SignoutsDebug.fields.oldLastName)
		val jpAttendanceId = new NullableIntFieldValue(self, SignoutsDebug.fields.jpAttendanceId)
		val apAttendanceId = new NullableIntFieldValue(self, SignoutsDebug.fields.apAttendanceId)
		val snapshotType = new NullableStringFieldValue(self, SignoutsDebug.fields.snapshotType)
		val snapshotDatetime = new NullableDateTimeFieldValue(self, SignoutsDebug.fields.snapshotDatetime)
		val snapshotId = new IntFieldValue(self, SignoutsDebug.fields.snapshotId)
	}
}

object SignoutsDebug extends StorableObject[SignoutsDebug] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "SIGNOUTS_DEBUG"

	object fields extends FieldsObject {
		val signoutId = new NullableIntDatabaseField(self, "SIGNOUT_ID")
		val programId = new IntDatabaseField(self, "PROGRAM_ID")
		val boatId = new IntDatabaseField(self, "BOAT_ID")
		val cardNum = new NullableStringDatabaseField(self, "CARD_NUM", 50)
		val sailNumber = new NullableStringDatabaseField(self, "SAIL_NUMBER", 15)
		val signoutDatetime = new NullableDateTimeDatabaseField(self, "SIGNOUT_DATETIME")
		val signinDatetime = new NullableDateTimeDatabaseField(self, "SIGNIN_DATETIME")
		val testRatingId = new NullableIntDatabaseField(self, "TEST_RATING_ID")
		val testResult = new NullableStringDatabaseField(self, "TEST_RESULT", 10)
		val signoutType = new StringDatabaseField(self, "SIGNOUT_TYPE", 1)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
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
		val snapshotType = new NullableStringDatabaseField(self, "SNAPSHOT_TYPE", 1)
		val snapshotDatetime = new NullableDateTimeDatabaseField(self, "SNAPSHOT_DATETIME")
		val snapshotId = new IntDatabaseField(self, "SNAPSHOT_ID")
	}

	def primaryKey: IntDatabaseField = fields.snapshotId
}