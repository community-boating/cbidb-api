package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class UserPreferenceValue extends StorableClass(UserPreferenceValue) {
	object values extends ValuesObject {
		val prefId = new IntFieldValue(self, UserPreferenceValue.fields.prefId)
		val prefAlias = new StringFieldValue(self, UserPreferenceValue.fields.prefAlias)
		val userId = new NullableIntFieldValue(self, UserPreferenceValue.fields.userId)
		val prefValue = new NullableBooleanFIeldValue(self, UserPreferenceValue.fields.prefValue)
	}
}

object UserPreferenceValue extends StorableObject[UserPreferenceValue] {
	val entityName: String = "USER_PREFERENCE_VALUES"

	object fields extends FieldsObject {
		val prefId = new IntDatabaseField(self, "PREF_ID")
		val prefAlias = new StringDatabaseField(self, "PREF_ALIAS", 50)
		val userId = new NullableIntDatabaseField(self, "USER_ID")
		val prefValue = new NullableBooleanDatabaseField(self, "PREF_VALUE")
	}
}