package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PersonsBoatsFlagsMax extends StorableClass(PersonsBoatsFlagsMax) {
	object values extends ValuesObject {
		val personId = new IntFieldValue(self, PersonsBoatsFlagsMax.fields.personId)
		val programId = new IntFieldValue(self, PersonsBoatsFlagsMax.fields.programId)
		val boatId = new NullableIntFieldValue(self, PersonsBoatsFlagsMax.fields.boatId)
		val maxFlag = new NullableStringFieldValue(self, PersonsBoatsFlagsMax.fields.maxFlag)
	}
}

object PersonsBoatsFlagsMax extends StorableObject[PersonsBoatsFlagsMax] {
	val entityName: String = "PERSONS_BOATS_FLAGS_MAX"

	object fields extends FieldsObject {
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val programId = new IntDatabaseField(self, "PROGRAM_ID")
		val boatId = new NullableIntDatabaseField(self, "BOAT_ID")
		val maxFlag = new NullableStringDatabaseField(self, "MAX_FLAG", 1)
	}
}