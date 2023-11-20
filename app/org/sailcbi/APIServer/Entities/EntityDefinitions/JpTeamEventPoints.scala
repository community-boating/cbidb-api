package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpTeamEventPoints extends StorableClass(JpTeamEventPoints) {
	override object references extends ReferencesObject {
		val jpTeam = new Initializable[JpTeam]
	}

	override object values extends ValuesObject {
		val rowId = new IntFieldValue(self, JpTeamEventPoints.fields.rowId)
		val eventId = new IntFieldValue(self, JpTeamEventPoints.fields.eventId)
		val teamId = new IntFieldValue(self, JpTeamEventPoints.fields.teamId)
		val points = new DoubleFieldValue(self, JpTeamEventPoints.fields.points)
	}
}

object JpTeamEventPoints extends StorableObject[JpTeamEventPoints] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "JP_TEAM_EVENT_POINTS"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val eventId = new IntDatabaseField(self, "EVENT_ID")
		val teamId = new IntDatabaseField(self, "TEAM_ID")
		val points = new DoubleDatabaseField(self, "POINTS")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}