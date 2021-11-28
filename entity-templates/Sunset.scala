package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class Sunset extends StorableClass(Sunset) {
	object values extends ValuesObject {
		val id = new IntFieldValue(self, Sunset.fields.id)
		val sunsetDatetime = new NullableLocalDateTimeFieldValue(self, Sunset.fields.sunsetDatetime)
	}
}

object Sunset extends StorableObject[Sunset] {
	val entityName: String = "SUNSETS"

	object fields extends FieldsObject {
		val id = new IntDatabaseField(self, "ID")
		val sunsetDatetime = new NullableLocalDateTimeDatabaseField(self, "SUNSET_DATETIME")
	}

	def primaryKey: IntDatabaseField = fields.id
}