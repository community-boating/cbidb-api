package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PersonsBoatsFlag extends StorableClass(PersonsBoatsFlag) {
	object values extends ValuesObject {
		val personId = new IntFieldValue(self, PersonsBoatsFlag.fields.personId)
		val programId = new IntFieldValue(self, PersonsBoatsFlag.fields.programId)
		val boatId = new NullableIntFieldValue(self, PersonsBoatsFlag.fields.boatId)
		val flag = new NullableBooleanFIeldValue(self, PersonsBoatsFlag.fields.flag)
		val flagRank = new NullableDoubleFieldValue(self, PersonsBoatsFlag.fields.flagRank)
	}
}

object PersonsBoatsFlag extends StorableObject[PersonsBoatsFlag] {
	val entityName: String = "PERSONS_BOATS_FLAGS"

	object fields extends FieldsObject {
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val programId = new IntDatabaseField(self, "PROGRAM_ID")
		val boatId = new NullableIntDatabaseField(self, "BOAT_ID")
		val flag = new NullableBooleanDatabaseField(self, "FLAG")
		val flagRank = new NullableDoubleDatabaseField(self, "FLAG_RANK")
	}
}