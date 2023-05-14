package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._

class SunsetTime extends StorableClass(SunsetTime) {
	object values extends ValuesObject {
		val rowId = new IntFieldValue(self, SunsetTime.fields.rowId)
		val forDate = new DateTimeFieldValue(self, SunsetTime.fields.forDate)
		val twilightStart = new DateTimeFieldValue(self, SunsetTime.fields.twilightStart)
		val sunrise = new DateTimeFieldValue(self, SunsetTime.fields.sunrise)
		val sunset = new DateTimeFieldValue(self, SunsetTime.fields.sunset)
		val twilightEnd = new DateTimeFieldValue(self, SunsetTime.fields.twilightEnd)
		val dayLengthSeconds = new DoubleFieldValue(self, SunsetTime.fields.dayLengthSeconds)
		val sonarNoon = new DateTimeFieldValue(self, SunsetTime.fields.sonarNoon)
		val nauticalTwilightStart = new DateTimeFieldValue(self, SunsetTime.fields.nauticalTwilightStart)
		val nauticalTwilightEnd = new DateTimeFieldValue(self, SunsetTime.fields.nauticalTwilightEnd)
		val astronomicalTwilightStart = new DateTimeFieldValue(self, SunsetTime.fields.astronomicalTwilightStart)
		val astronomicalTwilightEnd = new DateTimeFieldValue(self, SunsetTime.fields.astronomicalTwilightEnd)
	}
}

object SunsetTime extends StorableObject[SunsetTime] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "SUNSET_TIMES"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val forDate = new DateTimeDatabaseField(self, "FOR_DATE")
		val twilightStart = new DateTimeDatabaseField(self, "TWILIGHT_START")
		val sunrise = new DateTimeDatabaseField(self, "SUNRISE")
		val sunset = new DateTimeDatabaseField(self, "SUNSET")
		val twilightEnd = new DateTimeDatabaseField(self, "TWILIGHT_END")
		val dayLengthSeconds = new DoubleDatabaseField(self, "DAY_LENGTH_SECONDS")
		val sonarNoon = new DateTimeDatabaseField(self, "SONAR_NOON")
		val nauticalTwilightStart = new DateTimeDatabaseField(self, "NAUTICAL_TWILIGHT_START")
		val nauticalTwilightEnd = new DateTimeDatabaseField(self, "NAUTICAL_TWILIGHT_END")
		val astronomicalTwilightStart = new DateTimeDatabaseField(self, "ASTRONOMICAL_TWILIGHT_START")
		val astronomicalTwilightEnd = new DateTimeDatabaseField(self, "ASTRONOMICAL_TWILIGHT_END")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}