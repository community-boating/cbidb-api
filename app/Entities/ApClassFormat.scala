package Entities

import Storable.Fields.FieldValue.{FieldValue, IntFieldValue, NullableStringFieldValue, StringFieldValue}
import Storable.Fields.{DatabaseField, IntDatabaseField, StringDatabaseField}
import Storable._

case class ApClassFormat (
  formatId: Int,
  typeId: Int,
  description: Option[String]
) extends StorableClass {
  def companion: StorableObject[ApClassFormat] = ApClassFormat
  object references extends ReferencesObject {
    var apClassType: Option[ApClassType] = None
  }
  def setApClassType(v: ApClassType): Unit = references.apClassType = Some(v)

  def getApClassType: ApClassType = references.apClassType match {
    case Some(x) => x
    case None => throw new Exception("ApClassType unset for ApClassFormat " + formatId)
  }

  def deconstruct: Set[FieldValue] = Set(
    IntFieldValue(ApClassFormat.fields.formatId, formatId),
    IntFieldValue(ApClassFormat.fields.typeId, typeId),
    NullableStringFieldValue(ApClassFormat.fields.description, description)
  )
}

object ApClassFormat extends StorableObject[ApClassFormat] {
  val entityName: String = "AP_CLASS_FORMATS"

  object fields extends FieldsObject {
    val formatId = new IntDatabaseField(self, "FORMAT_ID")
    val typeId = new IntDatabaseField(self, "TYPE_ID")
    val description = new StringDatabaseField(self, "DESCRIPTION", 100)

  }

  val fieldList: List[DatabaseField[_]] = List(
    fields.formatId,
    fields.typeId,
    fields.description
  )
  val primaryKeyName: String = fieldList.head.getFieldName

  def construct(r: DatabaseRow): ThisClass =
    new ApClassFormat(
      fields.formatId.getValue(r),
      fields.typeId.getValue(r),
      fields.description.getOptionValue(r)
    )

  def getSeedData: Set[ApClassFormat] = Set(
    ApClassFormat(1, 1, None),
    ApClassFormat(2, 2, None),
    ApClassFormat(3, 3, None)
  )
}