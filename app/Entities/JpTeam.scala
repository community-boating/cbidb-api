package Entities

import Storable.Fields.FieldValue.{IntFieldValue, StringFieldValue}
import Storable.Fields.{IntDatabaseField, StringDatabaseField}
import Storable._

class JpTeam extends StorableClass {
  object references extends ReferencesObject {}
  object values extends ValuesObject {
    val teamId = new IntFieldValue(self, JpTeam.fields.teamId)
    val teamName = new StringFieldValue(self, JpTeam.fields.teamName)
  }
}

object JpTeam extends StorableObject[JpTeam] {
  val entityName: String = "JP_TEAMS"

  object fields extends FieldsObject {
    val teamId = new IntDatabaseField(self, "TEAM_ID")
    val teamName = new StringDatabaseField(self, "TEAM_NAME", 100)
  }

  val primaryKey: IntDatabaseField = fields.teamId

  def getSeedData: Set[JpTeam] = Set()
}