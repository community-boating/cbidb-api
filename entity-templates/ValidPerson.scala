package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ValidPerson extends StorableClass(ValidPerson) {
	object values extends ValuesObject {
		val personId = new NullableIntFieldValue(self, ValidPerson.fields.personId)
	}
}

object ValidPerson extends StorableObject[ValidPerson] {
	val entityName: String = "VALID_PERSONS"

	object fields extends FieldsObject {
		val personId = new NullableIntDatabaseField(self, "PERSON_ID")
	}
}