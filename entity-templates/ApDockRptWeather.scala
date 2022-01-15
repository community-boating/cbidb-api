package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ApDockRptWeather extends StorableClass(ApDockRptWeather) {
	object values extends ValuesObject {
		val weatherId = new IntFieldValue(self, ApDockRptWeather.fields.weatherId)
		val drId = new NullableIntFieldValue(self, ApDockRptWeather.fields.drId)
		val time = new NullableStringFieldValue(self, ApDockRptWeather.fields.time)
		val temp = new NullableDoubleFieldValue(self, ApDockRptWeather.fields.temp)
		val weather = new NullableStringFieldValue(self, ApDockRptWeather.fields.weather)
		val windDir = new NullableStringFieldValue(self, ApDockRptWeather.fields.windDir)
		val windSteady = new NullableDoubleFieldValue(self, ApDockRptWeather.fields.windSteady)
		val windGust = new NullableDoubleFieldValue(self, ApDockRptWeather.fields.windGust)
		val flag = new NullableStringFieldValue(self, ApDockRptWeather.fields.flag)
		val restrictions = new NullableStringFieldValue(self, ApDockRptWeather.fields.restrictions)
		val createdOn = new NullableLocalDateTimeFieldValue(self, ApDockRptWeather.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ApDockRptWeather.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, ApDockRptWeather.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ApDockRptWeather.fields.updatedBy)
	}
}

object ApDockRptWeather extends StorableObject[ApDockRptWeather] {
	val entityName: String = "AP_DOCK_RPT_WEATHER"

	object fields extends FieldsObject {
		val weatherId = new IntDatabaseField(self, "WEATHER_ID")
		val drId = new NullableIntDatabaseField(self, "DR_ID")
		val time = new NullableStringDatabaseField(self, "TIME", 10)
		val temp = new NullableDoubleDatabaseField(self, "TEMP")
		val weather = new NullableStringDatabaseField(self, "WEATHER", 50)
		val windDir = new NullableStringDatabaseField(self, "WIND_DIR", 10)
		val windSteady = new NullableDoubleDatabaseField(self, "WIND_STEADY")
		val windGust = new NullableDoubleDatabaseField(self, "WIND_GUST")
		val flag = new NullableStringDatabaseField(self, "FLAG", 20)
		val restrictions = new NullableStringDatabaseField(self, "RESTRICTIONS", 4000)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.weatherId
}