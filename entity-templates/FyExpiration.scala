package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FyExpiration extends StorableClass(FyExpiration) {
	object values extends ValuesObject {
		val personId = new IntFieldValue(self, FyExpiration.fields.personId)
		val assignId = new NullableIntFieldValue(self, FyExpiration.fields.assignId)
	}
}

object FyExpiration extends StorableObject[FyExpiration] {
	val entityName: String = "FY_EXPIRATIONS"

	object fields extends FieldsObject {
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val assignId = new NullableIntDatabaseField(self, "ASSIGN_ID")
	}
}