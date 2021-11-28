package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class UsersPreference extends StorableClass(UsersPreference) {
	object values extends ValuesObject {
		val rowId = new IntFieldValue(self, UsersPreference.fields.rowId)
		val userId = new NullableIntFieldValue(self, UsersPreference.fields.userId)
		val prefId = new NullableIntFieldValue(self, UsersPreference.fields.prefId)
		val createdOn = new NullableLocalDateTimeFieldValue(self, UsersPreference.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, UsersPreference.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, UsersPreference.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, UsersPreference.fields.updatedBy)
		val valueFakeboolean = new NullableBooleanFIeldValue(self, UsersPreference.fields.valueFakeboolean)
	}
}

object UsersPreference extends StorableObject[UsersPreference] {
	val entityName: String = "USERS_PREFERENCES"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val userId = new NullableIntDatabaseField(self, "USER_ID")
		val prefId = new NullableIntDatabaseField(self, "PREF_ID")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val valueFakeboolean = new NullableBooleanDatabaseField(self, "VALUE_FAKEBOOLEAN")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}