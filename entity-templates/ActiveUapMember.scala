package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ActiveUapMember extends StorableClass(ActiveUapMember) {
	object values extends ValuesObject {
		val personId = new IntFieldValue(self, ActiveUapMember.fields.personId)
	}
}

object ActiveUapMember extends StorableObject[ActiveUapMember] {
	val entityName: String = "ACTIVE_UAP_MEMBERS"

	object fields extends FieldsObject {
		val personId = new IntDatabaseField(self, "PERSON_ID")
	}
}