package Entities

import Storable.Fields.FieldValue.IntFieldValue
import Storable.Fields.IntDatabaseField
import Storable._

class JpTeamEventPoints extends StorableClass {
  this.setCompanion(JpTeamEventPoints)
  object references extends ReferencesObject {
    var jpTeam: Option[JpTeam] = None
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

  def getSeedData: Set[JpTeamEventPoints] = Set()
}