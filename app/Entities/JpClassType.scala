package Entities

import Storable.Fields.FieldValue.{FieldValue, IntFieldValue, StringFieldValue}
import Storable.Fields.{DatabaseField, IntDatabaseField, StringDatabaseField}
import Storable._

case class JpClassType (
  typeId: Int,
  typeName: String,
  displayOrder: Int
) extends StorableClass {
  def companion: StorableObject[JpClassType] = JpClassType
  object references extends ReferencesObject {}

  def deconstruct: Set[FieldValue] = Set(
    IntFieldValue(JpClassType.fields.typeId, typeId),
    StringFieldValue(JpClassType.fields.typeName, typeName),
    IntFieldValue(JpClassType.fields.displayOrder, displayOrder)
  )
}

object JpClassType extends StorableObject[JpClassType] {
  val entityName: String = "JP_CLASS_TYPES"

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
    new JpClassType(
      r.intFields.get("TYPE_ID") match { case Some(Some(x)) => x; case _ => -1},
      r.stringFields.get("TYPE_NAME") match { case Some(Some(x)) => x; case _ => "" },
      r.intFields.get("DISPLAY_ORDER") match { case Some(Some(x)) => x; case _ => -1}
    )

  def getSeedData: Set[JpClassType] = Set()
}