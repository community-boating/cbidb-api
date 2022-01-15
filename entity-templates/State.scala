package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class State extends StorableClass(State) {
	object values extends ValuesObject {
		val stateId = new IntFieldValue(self, State.fields.stateId)
		val abbrev = new NullableStringFieldValue(self, State.fields.abbrev)
		val stateName = new NullableStringFieldValue(self, State.fields.stateName)
	}
}

object State extends StorableObject[State] {
	val entityName: String = "STATES"

	object fields extends FieldsObject {
		val stateId = new IntDatabaseField(self, "STATE_ID")
		val abbrev = new NullableStringDatabaseField(self, "ABBREV", 5)
		val stateName = new NullableStringDatabaseField(self, "STATE_NAME", 70)
	}

	def primaryKey: IntDatabaseField = fields.stateId
}