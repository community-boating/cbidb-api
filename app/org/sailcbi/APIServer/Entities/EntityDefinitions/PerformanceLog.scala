package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class PerformanceLog extends StorableClass(PerformanceLog) {
	override object values extends ValuesObject {
		val logId = new IntFieldValue(self, PerformanceLog.fields.logId)
		val appId = new IntFieldValue(self, PerformanceLog.fields.appId)
		val connectTime = new DoubleFieldValue(self, PerformanceLog.fields.connectTime)
		val requestTime = new DoubleFieldValue(self, PerformanceLog.fields.requestTime)
		val responseTime = new DoubleFieldValue(self, PerformanceLog.fields.responseTime)
		val processTime = new DoubleFieldValue(self, PerformanceLog.fields.processTime)
		val totalTime = new DoubleFieldValue(self, PerformanceLog.fields.totalTime)
		val logDatetime = new DateTimeFieldValue(self, PerformanceLog.fields.logDatetime)
		val userName = new StringFieldValue(self, PerformanceLog.fields.userName)
	}
}

object PerformanceLog extends StorableObject[PerformanceLog] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "PERFORMANCE_LOG"

	object fields extends FieldsObject {
		val logId = new IntDatabaseField(self, "LOG_ID")
		@NullableInDatabase
		val appId = new IntDatabaseField(self, "APP_ID")
		@NullableInDatabase
		val connectTime = new DoubleDatabaseField(self, "CONNECT_TIME")
		@NullableInDatabase
		val requestTime = new DoubleDatabaseField(self, "REQUEST_TIME")
		@NullableInDatabase
		val responseTime = new DoubleDatabaseField(self, "RESPONSE_TIME")
		@NullableInDatabase
		val processTime = new DoubleDatabaseField(self, "PROCESS_TIME")
		@NullableInDatabase
		val totalTime = new DoubleDatabaseField(self, "TOTAL_TIME")
		@NullableInDatabase
		val logDatetime = new DateTimeDatabaseField(self, "LOG_DATETIME")
		@NullableInDatabase
		val userName = new StringDatabaseField(self, "USER_NAME", 100)
	}

	def primaryKey: IntDatabaseField = fields.logId
}