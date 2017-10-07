package Entities

import Storable.Fields.FieldValue.{IntFieldValue, StringFieldValue}
import Storable.Fields.{IntDatabaseField, StringDatabaseField}
import Storable._

class JpTeam extends StorableClass {
  def companion: StorableObject[JpTeam] = JpTeam
  object references extends ReferencesObject {}
  object values extends ValuesObject {
    val teamId = new IntFieldValue(JpTeam.fields.teamId)
    val teamName = new StringFieldValue(JpTeam.fields.teamName)
  }
}

object JpTeam extends StorableObject[JpTeam] {
  val entityName: String = "JP_TEAMS"

  object fields extends FieldsObject {
    val teamId = new IntDatabaseField(self, "TEAM_ID")
    val teamName = new StringDatabaseField(self, "TEAM_NAME", 100)
  }

  val primaryKeyName: String = fields.teamId.getFieldName

  def getSeedData: Set[JpTeam] = Set()
}