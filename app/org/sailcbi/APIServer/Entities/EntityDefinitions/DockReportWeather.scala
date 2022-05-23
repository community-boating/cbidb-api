package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class DockReportWeather extends StorableClass(DockReportWeather) {
	object values extends ValuesObject {
		val weatherId = new IntFieldValue(self, DockReportWeather.fields.weatherId)
		val dockReportId = new IntFieldValue(self, DockReportWeather.fields.dockReportId)
		val weatherDatetime = new NullableDateTimeFieldValue(self, DockReportWeather.fields.weatherDatetime)
		val temp = new NullableDoubleFieldValue(self, DockReportWeather.fields.temp)
		val weatherSummary = new NullableStringFieldValue(self, DockReportWeather.fields.weatherSummary)
		val windDir = new NullableStringFieldValue(self, DockReportWeather.fields.windDir)
		val windSpeedKts = new NullableDoubleFieldValue(self, DockReportWeather.fields.windSpeedKts)
		val restrictions = new NullableStringFieldValue(self, DockReportWeather.fields.restrictions)
	}
}

object DockReportWeather extends StorableObject[DockReportWeather] {
	val entityName: String = "DOCK_REPORT_WEATHER"

	object fields extends FieldsObject {
		val weatherId = new IntDatabaseField(self, "WEATHER_ID")
		val dockReportId = new IntDatabaseField(self, "DOCK_REPORT_ID")
		val weatherDatetime = new NullableDateTimeDatabaseField(self, "WEATHER_DATETIME")
		val temp = new NullableDoubleDatabaseField(self, "TEMP")
		val weatherSummary = new NullableStringDatabaseField(self, "WEATHER_SUMMARY", 50)
		val windDir = new NullableStringDatabaseField(self, "WIND_DIR", 5)
		val windSpeedKts = new NullableDoubleDatabaseField(self, "WIND_SPEED_KTS")
		val restrictions = new NullableStringDatabaseField(self, "RESTRICTIONS", 500)
	}

	def primaryKey: IntDatabaseField = fields.weatherId
}