package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpTeamEventPoint extends StorableClass(JpTeamEventPoint) {
	object values extends ValuesObject {
		val rowId = new IntFieldValue(self, JpTeamEventPoint.fields.rowId)
		val eventId = new NullableIntFieldValue(self, JpTeamEventPoint.fields.eventId)
		val teamId = new NullableIntFieldValue(self, JpTeamEventPoint.fields.teamId)
		val points = new NullableDoubleFieldValue(self, JpTeamEventPoint.fields.points)
	}
}

object JpTeamEventPoint extends StorableObject[JpTeamEventPoint] {
	val entityName: String = "JP_TEAM_EVENT_POINTS"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val eventId = new NullableIntDatabaseField(self, "EVENT_ID")
		val teamId = new NullableIntDatabaseField(self, "TEAM_ID")
		val points = new NullableDoubleDatabaseField(self, "POINTS")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}