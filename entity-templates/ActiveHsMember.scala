package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ActiveHsMember extends StorableClass(ActiveHsMember) {
	object values extends ValuesObject {
		val personId = new IntFieldValue(self, ActiveHsMember.fields.personId)
	}
}

object ActiveHsMember extends StorableObject[ActiveHsMember] {
	val entityName: String = "ACTIVE_HS_MEMBERS"

	object fields extends FieldsObject {
		val personId = new IntDatabaseField(self, "PERSON_ID")
	}
}