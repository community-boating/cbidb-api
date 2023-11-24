package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class State extends StorableClass(State) {
	override object values extends ValuesObject {
		val stateId = new IntFieldValue(self, State.fields.stateId)
		val abbrev = new StringFieldValue(self, State.fields.abbrev)
		val stateName = new StringFieldValue(self, State.fields.stateName)
	}
}

object State extends StorableObject[State] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "STATES"

	object fields extends FieldsObject {
		val stateId = new IntDatabaseField(self, "STATE_ID")
		@NullableInDatabase
		val abbrev = new StringDatabaseField(self, "ABBREV", 5)
		@NullableInDatabase
		val stateName = new StringDatabaseField(self, "STATE_NAME", 70)
	}

	def primaryKey: IntDatabaseField = fields.stateId
}