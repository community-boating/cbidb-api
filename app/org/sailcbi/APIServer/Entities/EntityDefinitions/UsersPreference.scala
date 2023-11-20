package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class UsersPreference extends StorableClass(UsersPreference) {
	override object values extends ValuesObject {
		val rowId = new IntFieldValue(self, UsersPreference.fields.rowId)
		val userId = new IntFieldValue(self, UsersPreference.fields.userId)
		val prefId = new IntFieldValue(self, UsersPreference.fields.prefId)
		val createdOn = new DateTimeFieldValue(self, UsersPreference.fields.createdOn)
		val createdBy = new StringFieldValue(self, UsersPreference.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, UsersPreference.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, UsersPreference.fields.updatedBy)
		val valueFakeboolean = new NullableBooleanFieldValue(self, UsersPreference.fields.valueFakeboolean)
	}
}

object UsersPreference extends StorableObject[UsersPreference] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "USERS_PREFERENCES"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val userId = new IntDatabaseField(self, "USER_ID")
		val prefId = new IntDatabaseField(self, "PREF_ID")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val valueFakeboolean = new NullableBooleanDatabaseField(self, "VALUE_FAKEBOOLEAN")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}