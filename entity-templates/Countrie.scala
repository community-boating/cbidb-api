package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class Countrie extends StorableClass(Countrie) {
	object values extends ValuesObject {
		val countryId = new IntFieldValue(self, Countrie.fields.countryId)
		val abbrev = new NullableStringFieldValue(self, Countrie.fields.abbrev)
		val countryName = new NullableStringFieldValue(self, Countrie.fields.countryName)
	}
}

object Countrie extends StorableObject[Countrie] {
	val entityName: String = "COUNTRIES"

	object fields extends FieldsObject {
		val countryId = new IntDatabaseField(self, "COUNTRY_ID")
		val abbrev = new NullableStringDatabaseField(self, "ABBREV", 30)
		val countryName = new NullableStringDatabaseField(self, "COUNTRY_NAME", 500)
	}

	def primaryKey: IntDatabaseField = fields.countryId
}