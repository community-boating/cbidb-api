package Entities

import Storable.Fields.FieldValue.FieldValue
import Storable.Fields.{DatabaseField, IntDatabaseField, StringDatabaseField}
import Storable._

case class JpTeam (
  teamId: Int,
  teamName: String
) extends StorableClass {
  def companion: StorableObject[JpTeam] = JpTeam
  object references extends ReferencesObject {}

  def deconstruct: Set[FieldValue] = Set(
    IntFieldValue(JpTeam.fields.teamId, teamId),
    StringFieldValue(JpTeam.fields.teamName, teamName)
  )
}

object JpTeam extends StorableObject[JpTeam] {
  val entityName: String = "JP_TEAMS"

  object fields extends FieldsObject {
    val teamId = new IntDatabaseField(self, "TEAM_ID")
    val teamName = new StringDatabaseField(self, "TEAM_NAME", 100)
  }

  val fieldList: List[DatabaseField[_]] = List(
    fields.teamId,
    fields.teamName
  )
  val primaryKeyName: String = fieldList.head.getFieldName

  def construct(r: DatabaseRow): ThisClass =
    new JpTeam(
      fields.teamId.getValue(r),
      fields.teamName.getValue(r)
    )

  def getSeedData: Set[JpTeam] = Set()
}