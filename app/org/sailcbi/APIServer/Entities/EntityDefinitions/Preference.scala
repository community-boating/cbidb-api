package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class Preference extends StorableClass(Preference) {
	override object values extends ValuesObject {
		val prefId = new IntFieldValue(self, Preference.fields.prefId)
		val dataType = new IntFieldValue(self, Preference.fields.dataType)
		val createdOn = new DateTimeFieldValue(self, Preference.fields.createdOn)
		val createdBy = new StringFieldValue(self, Preference.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, Preference.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, Preference.fields.updatedBy)
		val prefAlias = new StringFieldValue(self, Preference.fields.prefAlias)
		val prefDescription = new StringFieldValue(self, Preference.fields.prefDescription)
	}
}

object Preference extends StorableObject[Preference] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "PREFERENCES"

	object fields extends FieldsObject {
		val prefId = new IntDatabaseField(self, "PREF_ID")
		@NullableInDatabase
		val dataType = new IntDatabaseField(self, "DATA_TYPE")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val prefAlias = new StringDatabaseField(self, "PREF_ALIAS", 50)
		@NullableInDatabase
		val prefDescription = new StringDatabaseField(self, "PREF_DESCRIPTION", 1000)
	}

	def primaryKey: IntDatabaseField = fields.prefId
}