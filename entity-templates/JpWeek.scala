package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpWeek extends StorableClass(JpWeek) {
	object values extends ValuesObject {
		val season = new NullableDoubleFieldValue(self, JpWeek.fields.season)
		val monday = new NullableLocalDateTimeFieldValue(self, JpWeek.fields.monday)
		val sunday = new NullableLocalDateTimeFieldValue(self, JpWeek.fields.sunday)
		val week = new NullableDoubleFieldValue(self, JpWeek.fields.week)
		val weekAlias = new NullableStringFieldValue(self, JpWeek.fields.weekAlias)
	}
}

object JpWeek extends StorableObject[JpWeek] {
	val entityName: String = "JP_WEEKS"

	object fields extends FieldsObject {
		val season = new NullableDoubleDatabaseField(self, "SEASON")
		val monday = new NullableLocalDateTimeDatabaseField(self, "MONDAY")
		val sunday = new NullableLocalDateTimeDatabaseField(self, "SUNDAY")
		val week = new NullableDoubleDatabaseField(self, "WEEK")
		val weekAlias = new NullableStringDatabaseField(self, "WEEK_ALIAS", 11)
	}
}