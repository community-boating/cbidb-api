package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class SymonRun extends StorableClass(SymonRun) {
	object values extends ValuesObject {
		val runId = new IntFieldValue(self, SymonRun.fields.runId)
		val hostName = new NullableStringFieldValue(self, SymonRun.fields.hostName)
		val programName = new NullableStringFieldValue(self, SymonRun.fields.programName)
		val status = new NullableDoubleFieldValue(self, SymonRun.fields.status)
		val macAddress = new NullableStringFieldValue(self, SymonRun.fields.macAddress)
		val runDatetime = new NullableLocalDateTimeFieldValue(self, SymonRun.fields.runDatetime)
		val badHash = new NullableBooleanFIeldValue(self, SymonRun.fields.badHash)
		val argString = new NullableStringFieldValue(self, SymonRun.fields.argString)
		val symonVersion = new NullableStringFieldValue(self, SymonRun.fields.symonVersion)
	}
}

object SymonRun extends StorableObject[SymonRun] {
	val entityName: String = "SYMON_RUNS"

	object fields extends FieldsObject {
		val runId = new IntDatabaseField(self, "RUN_ID")
		val hostName = new NullableStringDatabaseField(self, "HOST_NAME", 50)
		val programName = new NullableStringDatabaseField(self, "PROGRAM_NAME", 100)
		val status = new NullableDoubleDatabaseField(self, "STATUS")
		val macAddress = new NullableStringDatabaseField(self, "MAC_ADDRESS", 50)
		val runDatetime = new NullableLocalDateTimeDatabaseField(self, "RUN_DATETIME")
		val badHash = new NullableBooleanDatabaseField(self, "BAD_HASH")
		val argString = new NullableStringDatabaseField(self, "ARG_STRING", 100)
		val symonVersion = new NullableStringDatabaseField(self, "SYMON_VERSION", 5)
	}

	def primaryKey: IntDatabaseField = fields.runId
}