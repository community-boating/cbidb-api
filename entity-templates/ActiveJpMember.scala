package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ActiveJpMember extends StorableClass(ActiveJpMember) {
	object values extends ValuesObject {
		val personId = new IntFieldValue(self, ActiveJpMember.fields.personId)
	}
}

object ActiveJpMember extends StorableObject[ActiveJpMember] {
	val entityName: String = "ACTIVE_JP_MEMBERS"

	object fields extends FieldsObject {
		val personId = new IntDatabaseField(self, "PERSON_ID")
	}
}