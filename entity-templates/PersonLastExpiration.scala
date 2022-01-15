package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PersonLastExpiration extends StorableClass(PersonLastExpiration) {
	object values extends ValuesObject {
		val personId = new IntFieldValue(self, PersonLastExpiration.fields.personId)
		val lastExpiration = new NullableLocalDateTimeFieldValue(self, PersonLastExpiration.fields.lastExpiration)
	}
}

object PersonLastExpiration extends StorableObject[PersonLastExpiration] {
	val entityName: String = "PERSON_LAST_EXPIRATIONS"

	object fields extends FieldsObject {
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val lastExpiration = new NullableLocalDateTimeDatabaseField(self, "LAST_EXPIRATION")
	}
}