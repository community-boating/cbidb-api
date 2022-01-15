package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class DatesWithSeason extends StorableClass(DatesWithSeason) {
	object values extends ValuesObject {
		val datecol = new NullableLocalDateTimeFieldValue(self, DatesWithSeason.fields.datecol)
		val season = new NullableDoubleFieldValue(self, DatesWithSeason.fields.season)
	}
}

object DatesWithSeason extends StorableObject[DatesWithSeason] {
	val entityName: String = "DATES_WITH_SEASONS"

	object fields extends FieldsObject {
		val datecol = new NullableLocalDateTimeDatabaseField(self, "DATECOL")
		val season = new NullableDoubleDatabaseField(self, "SEASON")
	}
}