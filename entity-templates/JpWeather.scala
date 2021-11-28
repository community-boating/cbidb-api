package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpWeather extends StorableClass(JpWeather) {
	object values extends ValuesObject {
		val weatherId = new IntFieldValue(self, JpWeather.fields.weatherId)
		val drDate = new NullableLocalDateTimeFieldValue(self, JpWeather.fields.drDate)
		val weatherTime = new NullableStringFieldValue(self, JpWeather.fields.weatherTime)
		val conditions = new NullableStringFieldValue(self, JpWeather.fields.conditions)
		val temp = new NullableDoubleFieldValue(self, JpWeather.fields.temp)
		val windDir = new NullableStringFieldValue(self, JpWeather.fields.windDir)
		val windSteady = new NullableDoubleFieldValue(self, JpWeather.fields.windSteady)
		val windGust = new NullableDoubleFieldValue(self, JpWeather.fields.windGust)
		val flag = new NullableStringFieldValue(self, JpWeather.fields.flag)
		val restrictions = new NullableStringFieldValue(self, JpWeather.fields.restrictions)
		val createdOn = new NullableLocalDateTimeFieldValue(self, JpWeather.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpWeather.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, JpWeather.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpWeather.fields.updatedBy)
	}
}

object JpWeather extends StorableObject[JpWeather] {
	val entityName: String = "JP_WEATHER"

	object fields extends FieldsObject {
		val weatherId = new IntDatabaseField(self, "WEATHER_ID")
		val drDate = new NullableLocalDateTimeDatabaseField(self, "DR_DATE")
		val weatherTime = new NullableStringDatabaseField(self, "WEATHER_TIME", 20)
		val conditions = new NullableStringDatabaseField(self, "CONDITIONS", 100)
		val temp = new NullableDoubleDatabaseField(self, "TEMP")
		val windDir = new NullableStringDatabaseField(self, "WIND_DIR", 5)
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