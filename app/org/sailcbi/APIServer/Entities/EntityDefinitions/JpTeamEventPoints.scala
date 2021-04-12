package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.framework.Storable.FieldValues.IntFieldValue
import com.coleji.framework.Storable.Fields.IntDatabaseField
import com.coleji.framework.Storable._
import com.coleji.framework.Util.Initializable

class JpTeamEventPoints extends StorableClass(JpTeamEventPoints) {
	override object references extends ReferencesObject {
		val jpTeam = new Initializable[JpTeam]
	}

	object values extends ValuesObject {
		val rowId = new IntFieldValue(self, JpTeamEventPoints.fields.rowId)
		val teamId = new IntFieldValue(self, JpTeamEventPoints.fields.teamId)
		val points = new IntFieldValue(self, JpTeamEventPoints.fields.points)
	}
}

object JpTeamEventPoints extends StorableObject[JpTeamEventPoints] {
	val entityName: String = "JP_TEAM_EVENT_POINTS"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val teamId = new IntDatabaseField(self, "TEAM_ID")
		val points = new IntDatabaseField(self, "POINTS")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}