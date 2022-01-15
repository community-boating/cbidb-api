package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class SymonSchedule extends StorableClass(SymonSchedule) {
	object values extends ValuesObject {
		val scheduleId = new IntFieldValue(self, SymonSchedule.fields.scheduleId)
		val hostName = new NullableStringFieldValue(self, SymonSchedule.fields.hostName)
		val programName = new NullableStringFieldValue(self, SymonSchedule.fields.programName)
		val argString = new NullableStringFieldValue(self, SymonSchedule.fields.argString)
		val freqDays = new NullableDoubleFieldValue(self, SymonSchedule.fields.freqDays)
		val createdOn = new NullableLocalDateTimeFieldValue(self, SymonSchedule.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, SymonSchedule.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, SymonSchedule.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, SymonSchedule.fields.updatedBy)
		val symonVersion = new NullableStringFieldValue(self, SymonSchedule.fields.symonVersion)
	}
}

object SymonSchedule extends StorableObject[SymonSchedule] {
	val entityName: String = "SYMON_SCHEDULE"

	object fields extends FieldsObject {
		val scheduleId = new IntDatabaseField(self, "SCHEDULE_ID")
		val hostName = new NullableStringDatabaseField(self, "HOST_NAME", 50)
		val programName = new NullableStringDatabaseField(self, "PROGRAM_NAME", 50)
		val argString = new NullableStringDatabaseField(self, "ARG_STRING", 100)
		val freqDays = new NullableDoubleDatabaseField(self, "FREQ_DAYS")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val symonVersion = new NullableStringDatabaseField(self, "SYMON_VERSION", 5)
	}

	def primaryKey: IntDatabaseField = fields.scheduleId
}