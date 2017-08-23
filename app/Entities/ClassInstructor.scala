package Entities

import Storable.Fields.{DatabaseField, IntDatabaseField, StringDatabaseField}
import Storable._

case class ClassInstructor(
  instructorId: Int,
  nameFirst: String,
  nameLast: String
) extends StorableClass {
  object references extends ReferencesObject {}
}

object ClassInstructor extends StorableObject[ClassInstructor] {
  val entityName: String = "CLASS_INSTRUCTORS"

  object fields extends FieldsObject {
    val instructorId = new IntDatabaseField(self, "INSTRUCTOR_ID")
    val nameFirst = new StringDatabaseField(self, "NAME_FIRST", 100)
    val nameLast = new StringDatabaseField(self, "NAME_LAST", 100)
  }

  val fieldList: List[DatabaseField] = List(
    fields.instructorId,
    fields.nameFirst,
    fields.nameLast
  )
  val primaryKeyName: String = fieldList.head.getFieldName

  def construct(r: DatabaseRow): ThisClass =
    new ClassInstructor(
      r.intFields.get("INSTRUCTOR_ID") match { case Some(Some(x)) => x; case None => -1},
      r.stringFields.get("NAME_FIRST") match { case Some(Some(x)) => x; case None => ""},
      r.stringFields.get("NAME_LAST") match { case Some(Some(x)) => x; case None => ""}
    )

  def getTestData: Set[ClassInstructor] = Set()
}