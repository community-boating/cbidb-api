package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class Countrie extends StorableClass(Countrie) {
	override object values extends ValuesObject {
		val countryId = new IntFieldValue(self, Countrie.fields.countryId)
		val abbrev = new StringFieldValue(self, Countrie.fields.abbrev)
		val countryName = new StringFieldValue(self, Countrie.fields.countryName)
	}
}

object Countrie extends StorableObject[Countrie] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "COUNTRIES"

	object fields extends FieldsObject {
		val countryId = new IntDatabaseField(self, "COUNTRY_ID")
		@NullableInDatabase
		val abbrev = new StringDatabaseField(self, "ABBREV", 30)
		@NullableInDatabase
		val countryName = new StringDatabaseField(self, "COUNTRY_NAME", 500)
	}

	def primaryKey: IntDatabaseField = fields.countryId
}