package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class Sunset extends StorableClass(Sunset) {
	override object values extends ValuesObject {
		val id = new IntFieldValue(self, Sunset.fields.id)
		val sunsetDatetime = new DateTimeFieldValue(self, Sunset.fields.sunsetDatetime)
	}
}

object Sunset extends StorableObject[Sunset] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "SUNSETS"

	object fields extends FieldsObject {
		val id = new IntDatabaseField(self, "ID")
		@NullableInDatabase
		val sunsetDatetime = new DateTimeDatabaseField(self, "SUNSET_DATETIME")
	}

	def primaryKey: IntDatabaseField = fields.id
}