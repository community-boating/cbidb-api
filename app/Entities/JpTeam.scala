package Entities

import Storable.Fields.FieldValue.{FieldValue, IntFieldValue, StringFieldValue}
import Storable.Fields.{DatabaseField, IntDatabaseField, StringDatabaseField}
import Storable._

case class JpTeam (
  teamId: Int,
  teamName: String
) extends StorableClass {
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

  val fieldList: List[DatabaseField] = List(
    fields.teamId,
    fields.teamName
  )
  val primaryKeyName: String = fieldList.head.getFieldName

  def construct(r: DatabaseRow): ThisClass =
    new JpTeam(
      r.intFields.get("TEAM_ID") match { case Some(Some(x)) => x; case _ => -1},
      r.stringFields.get("TEAM_NAME") match { case Some(Some(x)) => x; case _ => "" }
    )

  def getTestData: Set[JpTeam] = Set()
}