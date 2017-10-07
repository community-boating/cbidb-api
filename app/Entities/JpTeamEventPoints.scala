package Entities

import Storable.Fields.FieldValue.FieldValue
import Storable.Fields.{DatabaseField, IntDatabaseField}
import Storable._

case class JpTeamEventPoints (
  rowId: Int,
  teamId: Int,
  points: Int
) extends StorableClass {
  def companion: StorableObject[JpTeamEventPoints] = JpTeamEventPoints
  object references extends ReferencesObject {
    var jpTeam: Option[JpTeam] = None
  }

  def deconstruct: Set[FieldValue] = Set(
    IntFieldValue(JpTeamEventPoints.fields.rowId, rowId),
    IntFieldValue(JpTeamEventPoints.fields.teamId, teamId),
    IntFieldValue(JpTeamEventPoints.fields.points, points)
  )
}

object JpTeamEventPoints extends StorableObject[JpTeamEventPoints] {
  val entityName: String = "JP_TEAM_EVENT_POINTS"

  object fields extends FieldsObject {
    val rowId = new IntDatabaseField(self, "ROW_ID")
    val teamId = new IntDatabaseField(self, "TEAM_ID")
    val points = new IntDatabaseField(self, "POINTS")
  }

  val fieldList: List[DatabaseField[_]] = List(
    fields.rowId,
    fields.teamId,
    fields.points
  )
  val primaryKeyName: String = fieldList.head.getFieldName

  def construct(r: DatabaseRow): ThisClass =
    new JpTeamEventPoints(
      fields.rowId.getValue(r),
      fields.teamId.getValue(r),
      fields.points.getValue(r)
    )

  def getSeedData: Set[JpTeamEventPoints] = Set()
}