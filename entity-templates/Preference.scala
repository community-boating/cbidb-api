package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class Preference extends StorableClass(Preference) {
	object values extends ValuesObject {
		val prefId = new IntFieldValue(self, Preference.fields.prefId)
		val dataType = new NullableDoubleFieldValue(self, Preference.fields.dataType)
		val createdOn = new NullableLocalDateTimeFieldValue(self, Preference.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, Preference.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, Preference.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, Preference.fields.updatedBy)
		val prefAlias = new StringFieldValue(self, Preference.fields.prefAlias)
		val prefDescription = new NullableStringFieldValue(self, Preference.fields.prefDescription)
	}
}

object Preference extends StorableObject[Preference] {
	val entityName: String = "PREFERENCES"

	object fields extends FieldsObject {
		val prefId = new IntDatabaseField(self, "PREF_ID")
		val dataType = new NullableDoubleDatabaseField(self, "DATA_TYPE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val prefAlias = new StringDatabaseField(self, "PREF_ALIAS", 50)
		val prefDescription = new NullableStringDatabaseField(self, "PREF_DESCRIPTION", 1000)
	}

	def primaryKey: IntDatabaseField = fields.prefId
}