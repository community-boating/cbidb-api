package Entities

import Storable.Fields.FieldValue.{FieldValue, IntFieldValue, StringFieldValue}
import Storable.Fields.{DatabaseField, IntDatabaseField, StringDatabaseField}
import Storable._

case class ClassInstructor(
  instructorId: Int,
  nameFirst: String,
  nameLast: String
) extends StorableClass {
  def companion: StorableObject[ClassInstructor] = ClassInstructor
  object references extends ReferencesObject {}

  def deconstruct: Set[FieldValue] = Set(
    IntFieldValue(ClassInstructor.fields.instructorId, instructorId),
    StringFieldValue(ClassInstructor.fields.nameFirst, nameFirst),
    StringFieldValue(ClassInstructor.fields.nameLast, nameLast)
  )
}

object ClassInstructor extends StorableObject[ClassInstructor] {
  val entityName: String = "CLASS_INSTRUCTORS"

  object fields extends FieldsObject {
    val instructorId = new IntDatabaseField(self, "INSTRUCTOR_ID")
    val nameFirst = new StringDatabaseField(self, "NAME_FIRST", 100)
    val nameLast = new StringDatabaseField(self, "NAME_LAST", 100)
  }

  val fieldList: List[DatabaseField[_]] = List(
    fields.instructorId,
    fields.nameFirst,
    fields.nameLast
  )
  val primaryKeyName: String = fieldList.head.getFieldName

  def construct(r: DatabaseRow): ThisClass =
    new ClassInstructor(
      fields.instructorId.getValue(r),
      fields.nameFirst.getValue(r),
      fields.nameLast.getValue(r)
    )

  def getSeedData: Set[ClassInstructor] = Set()
}