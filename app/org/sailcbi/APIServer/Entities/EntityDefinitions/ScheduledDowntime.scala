package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ScheduledDowntime extends StorableClass(ScheduledDowntime) {
	override object values extends ValuesObject {
		val scheduleId = new IntFieldValue(self, ScheduledDowntime.fields.scheduleId)
		val startDatetime = new NullableDateTimeFieldValue(self, ScheduledDowntime.fields.startDatetime)
		val endDatetime = new NullableDateTimeFieldValue(self, ScheduledDowntime.fields.endDatetime)
	}
}

object ScheduledDowntime extends StorableObject[ScheduledDowntime] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "SCHEDULED_DOWNTIME"

	object fields extends FieldsObject {
		val scheduleId = new IntDatabaseField(self, "SCHEDULE_ID")
		val startDatetime = new NullableDateTimeDatabaseField(self, "START_DATETIME")
		val endDatetime = new NullableDateTimeDatabaseField(self, "END_DATETIME")
	}

	def primaryKey: IntDatabaseField = fields.scheduleId
}