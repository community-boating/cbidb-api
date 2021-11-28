package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PerformanceLog extends StorableClass(PerformanceLog) {
	object values extends ValuesObject {
		val logId = new IntFieldValue(self, PerformanceLog.fields.logId)
		val appId = new NullableIntFieldValue(self, PerformanceLog.fields.appId)
		val connectTime = new NullableDoubleFieldValue(self, PerformanceLog.fields.connectTime)
		val requestTime = new NullableDoubleFieldValue(self, PerformanceLog.fields.requestTime)
		val responseTime = new NullableDoubleFieldValue(self, PerformanceLog.fields.responseTime)
		val processTime = new NullableDoubleFieldValue(self, PerformanceLog.fields.processTime)
		val totalTime = new NullableDoubleFieldValue(self, PerformanceLog.fields.totalTime)
		val logDatetime = new NullableLocalDateTimeFieldValue(self, PerformanceLog.fields.logDatetime)
		val userName = new NullableStringFieldValue(self, PerformanceLog.fields.userName)
	}
}

object PerformanceLog extends StorableObject[PerformanceLog] {
	val entityName: String = "PERFORMANCE_LOG"

	object fields extends FieldsObject {
		val logId = new IntDatabaseField(self, "LOG_ID")
		val appId = new NullableIntDatabaseField(self, "APP_ID")
		val connectTime = new NullableDoubleDatabaseField(self, "CONNECT_TIME")
		val requestTime = new NullableDoubleDatabaseField(self, "REQUEST_TIME")
		val responseTime = new NullableDoubleDatabaseField(self, "RESPONSE_TIME")
		val processTime = new NullableDoubleDatabaseField(self, "PROCESS_TIME")
		val totalTime = new NullableDoubleDatabaseField(self, "TOTAL_TIME")
		val logDatetime = new NullableLocalDateTimeDatabaseField(self, "LOG_DATETIME")
		val userName = new NullableStringDatabaseField(self, "USER_NAME", 100)
	}

	def primaryKey: IntDatabaseField = fields.logId
}