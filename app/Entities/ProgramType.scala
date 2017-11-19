package Entities

import Storable.Fields.FieldValue.{IntFieldValue, StringFieldValue}
import Storable.Fields.{IntDatabaseField, StringDatabaseField}
import Storable._

class ProgramType extends StorableClass {
  this.setCompanion(ProgramType)
  object references extends ReferencesObject {}
  object values extends ValuesObject {
    val programId = new IntFieldValue(self, ProgramType.fields.programId)
    val programName = new StringFieldValue(self, ProgramType.fields.programName)
  }
}

object ProgramType extends StorableObject[ProgramType] {
  val entityName: String = "PROGRAM_TYPES"

  object fields extends FieldsObject {
    val programId = new IntDatabaseField(self, "PROGRAM_ID")
    val programName = new StringDatabaseField(self, "PROGRAM_NAME", 100)
  }

  def primaryKey: IntDatabaseField = fields.programId

  def getSeedData: Set[ProgramType] = Set()
}