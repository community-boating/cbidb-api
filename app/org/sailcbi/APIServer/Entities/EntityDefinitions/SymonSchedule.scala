package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class SymonSchedule extends StorableClass(SymonSchedule) {
	override object values extends ValuesObject {
		val scheduleId = new IntFieldValue(self, SymonSchedule.fields.scheduleId)
		val hostName = new StringFieldValue(self, SymonSchedule.fields.hostName)
		val programName = new StringFieldValue(self, SymonSchedule.fields.programName)
		val argString = new NullableStringFieldValue(self, SymonSchedule.fields.argString)
		val freqDays = new DoubleFieldValue(self, SymonSchedule.fields.freqDays)
		val createdOn = new NullableDateTimeFieldValue(self, SymonSchedule.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, SymonSchedule.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, SymonSchedule.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, SymonSchedule.fields.updatedBy)
		val symonVersion = new StringFieldValue(self, SymonSchedule.fields.symonVersion)
		val disabled = new NullableBooleanFieldValue(self, SymonSchedule.fields.disabled)
	}
}

object SymonSchedule extends StorableObject[SymonSchedule] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "SYMON_SCHEDULE"

	object fields extends FieldsObject {
		val scheduleId = new IntDatabaseField(self, "SCHEDULE_ID")
		val hostName = new StringDatabaseField(self, "HOST_NAME", 50)
		val programName = new StringDatabaseField(self, "PROGRAM_NAME", 50)
		val argString = new NullableStringDatabaseField(self, "ARG_STRING", 250)
		val freqDays = new DoubleDatabaseField(self, "FREQ_DAYS")
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val symonVersion = new StringDatabaseField(self, "SYMON_VERSION", 5)
		val disabled = new NullableBooleanDatabaseField(self, "DISABLED")
	}

	def primaryKey: IntDatabaseField = fields.scheduleId
}