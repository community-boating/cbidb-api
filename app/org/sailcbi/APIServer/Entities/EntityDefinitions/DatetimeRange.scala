package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class DatetimeRange extends StorableClass(DatetimeRange) {
	override object values extends ValuesObject {
		val rangeId = new IntFieldValue(self, DatetimeRange.fields.rangeId)
		val rangeType = new StringFieldValue(self, DatetimeRange.fields.rangeType)
		val startDatetime = new DateTimeFieldValue(self, DatetimeRange.fields.startDatetime)
		val endDatetime = new DateTimeFieldValue(self, DatetimeRange.fields.endDatetime)
	}
}

object DatetimeRange extends StorableObject[DatetimeRange] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "DATETIME_RANGES"

	object fields extends FieldsObject {
		val rangeId = new IntDatabaseField(self, "RANGE_ID")
		val rangeType = new StringDatabaseField(self, "RANGE_TYPE", 50)
		val startDatetime = new DateTimeDatabaseField(self, "START_DATETIME")
		val endDatetime = new DateTimeDatabaseField(self, "END_DATETIME")
	}

	def primaryKey: IntDatabaseField = fields.rangeId
}