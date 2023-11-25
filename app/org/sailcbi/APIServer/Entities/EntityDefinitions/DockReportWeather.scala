package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class DockReportWeather extends StorableClass(DockReportWeather) {
	override object values extends ValuesObject {
		val weatherId = new IntFieldValue(self, DockReportWeather.fields.weatherId)
		val dockReportId = new IntFieldValue(self, DockReportWeather.fields.dockReportId)
		val weatherDatetime = new DateTimeFieldValue(self, DockReportWeather.fields.weatherDatetime)
		val temp = new NullableDoubleFieldValue(self, DockReportWeather.fields.temp)
		val weatherSummary = new NullableStringFieldValue(self, DockReportWeather.fields.weatherSummary)
		val windDir = new NullableStringFieldValue(self, DockReportWeather.fields.windDir)
		val restrictions = new NullableStringFieldValue(self, DockReportWeather.fields.restrictions)
		val createdOn = new NullableDateTimeFieldValue(self, DockReportWeather.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, DockReportWeather.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, DockReportWeather.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, DockReportWeather.fields.updatedBy)
		val windSpeedKtsSteady = new NullableDoubleFieldValue(self, DockReportWeather.fields.windSpeedKtsSteady)
		val windSpeedKtsGust = new NullableDoubleFieldValue(self, DockReportWeather.fields.windSpeedKtsGust)
	}
}

object DockReportWeather extends StorableObject[DockReportWeather] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "DOCK_REPORT_WEATHER"

	object fields extends FieldsObject {
		val weatherId = new IntDatabaseField(self, "WEATHER_ID")
		val dockReportId = new IntDatabaseField(self, "DOCK_REPORT_ID")
		@NullableInDatabase
		val weatherDatetime = new DateTimeDatabaseField(self, "WEATHER_DATETIME")
		val temp = new NullableDoubleDatabaseField(self, "TEMP")
		val weatherSummary = new NullableStringDatabaseField(self, "WEATHER_SUMMARY", 50)
		val windDir = new NullableStringDatabaseField(self, "WIND_DIR", 5)
		val restrictions = new NullableStringDatabaseField(self, "RESTRICTIONS", 500)
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 100)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 100)
		val windSpeedKtsSteady = new NullableDoubleDatabaseField(self, "WIND_SPEED_KTS_STEADY")
		val windSpeedKtsGust = new NullableDoubleDatabaseField(self, "WIND_SPEED_KTS_GUST")
	}

	def primaryKey: IntDatabaseField = fields.weatherId
}