package Entities

import Storable.Fields.{DatabaseField, IntDatabaseField, StringDatabaseField}
import Storable._

case class ApClassType (
  typeId: Int,
  typeName: String,
  displayOrder: Int
) extends StorableClass {
  object references extends ReferencesObject {}


}

object ApClassType extends StorableObject[ApClassType] {
  val entityName: String = "AP_CLASS_TYPES"

  object fields extends FieldsObject {
    val typeId = new IntDatabaseField(self, "TYPE_ID")
    val typeName = new StringDatabaseField(self, "TYPE_NAME", 50)
    val displayOrder = new IntDatabaseField(self, "DISPLAY_ORDER")
  }

  val fieldList: List[DatabaseField] = List(
    fields.typeId,
    fields.typeName,
    fields.displayOrder
  )
  val primaryKeyName: String = fieldList.head.getFieldName

  def construct(r: DatabaseRow): ThisClass =
    new ApClassType(
      r.intFields.get("TYPE_ID") match { case Some(Some(x)) => x; case _ => -1},
      r.stringFields.get("TYPE_NAME") match { case Some(Some(x)) => x; case _ => "" },
      r.intFields.get("DISPLAY_ORDER") match { case Some(Some(x)) => x; case _ => -1}
    )

  def getTestData: Set[ApClassType] = Set(
    ApClassType(1, "Sailing 101", 1),
    ApClassType(2, "Sailing 102", 2),
    ApClassType(3, "Moar sailing", 3)
  )
}