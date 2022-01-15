package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ActiveApMember extends StorableClass(ActiveApMember) {
	object values extends ValuesObject {
		val personId = new IntFieldValue(self, ActiveApMember.fields.personId)
	}
}

object ActiveApMember extends StorableObject[ActiveApMember] {
	val entityName: String = "ACTIVE_AP_MEMBERS"

	object fields extends FieldsObject {
		val personId = new IntDatabaseField(self, "PERSON_ID")
	}
}