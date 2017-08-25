package Entities

import Storable.Fields.FieldValue.{FieldValue, IntFieldValue, StringFieldValue}
import Storable.Fields.{DatabaseField, IntDatabaseField, StringDatabaseField}
import Storable._

case class JpTeamEventPoints (
  rowId: Int,
  teamId: Int,
  points: Int
) extends StorableClass {
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

  val fieldList: List[DatabaseField] = List(
    fields.rowId,
    fields.teamId,
    fields.points
  )
  val primaryKeyName: String = fieldList.head.getFieldName

  def construct(r: DatabaseRow): ThisClass =
    new JpTeamEventPoints(
      r.intFields.get("ROW_ID") match { case Some(Some(x)) => x; case _ => -1},
      r.intFields.get("TEAM_ID") match { case Some(Some(x)) => x; case _ => -1},
      r.intFields.get("POINTS") match { case Some(Some(x)) => x; case _ => 0}
    )

  def getTestData: Set[JpTeamEventPoints] = Set()
}