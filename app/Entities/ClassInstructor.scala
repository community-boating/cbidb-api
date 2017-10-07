package Entities

import Storable.Fields.FieldValue.{FieldValue, IntFieldValue, StringFieldValue}
import Storable.Fields.{DatabaseField, IntDatabaseField, StringDatabaseField}
import Storable._

class ClassInstructor extends StorableClass {
  def companion: StorableObject[ClassInstructor] = ClassInstructor
  object references extends ReferencesObject {}
  object fields extends FieldsObject {
    val instructorId = new IntFieldValue(ClassInstructor.fields.instructorId)
    val nameFirst = new StringFieldValue(ClassInstructor.fields.nameFirst)
    val nameLast = new StringFieldValue(ClassInstructor.fields.nameLast)
  }
}

object ClassInstructor extends StorableObject[ClassInstructor] {
  val entityName: String = "CLASS_INSTRUCTORS"

  object fields extends FieldsObject {
    val instructorId = new IntDatabaseField(self, "INSTRUCTOR_ID")
    val nameFirst = new StringDatabaseField(self, "NAME_FIRST", 100)
    val nameLast = new StringDatabaseField(self, "NAME_LAST", 100)
  }

  val primaryKeyName: String = fields.instructorId.getFieldName


  def getSeedData: Set[ClassInstructor] = Set()
}