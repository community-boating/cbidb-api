package Entities

import Storable.Fields.FieldValue.{FieldValue, IntFieldValue, StringFieldValue}
import Storable.Fields.{DatabaseField, IntDatabaseField, StringDatabaseField}
import Storable._

case class ApClassType (
  typeId: Int,
  typeName: String,
  displayOrder: Int
) extends StorableClass {
  def companion: StorableObject[ApClassType] = ApClassType
  object references extends ReferencesObject {}

  def deconstruct: Set[FieldValue] = Set(
    IntFieldValue(ApClassType.fields.typeId, typeId),
    StringFieldValue(ApClassType.fields.typeName, typeName),
    IntFieldValue(ApClassType.fields.displayOrder, displayOrder)
  )
}

object ApClassType extends StorableObject[ApClassType] {
  val entityName: String = "AP_CLASS_TYPES"

  object fields extends FieldsObject {
    val typeId = new IntDatabaseField(self, "TYPE_ID")
    val typeName = new StringDatabaseField(self, "TYPE_NAME", 50)
    val displayOrder = new IntDatabaseField(self, "DISPLAY_ORDER")
  }

  val fieldList: List[DatabaseField[_]] = List(
    fields.typeId,
    fields.typeName,
    fields.displayOrder
  )
  val primaryKeyName: String = fieldList.head.getFieldName

  def construct(r: DatabaseRow): ThisClass =
    new ApClassType(
      fields.typeId.getValue(r),
      fields.typeName.getValue(r),
      fields.displayOrder.getValue(r)
    )

  def getSeedData: Set[ApClassType] = Set(
    ApClassType(1, "Sailing 101", 1),
    ApClassType(2, "Sailing 102", 2),
    ApClassType(3, "Moar sailing", 3)
  )
}