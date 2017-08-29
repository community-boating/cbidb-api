package Entities

import Storable.Fields.FieldValue.{FieldValue, IntFieldValue, StringFieldValue}
import Storable.Fields.{DatabaseField, IntDatabaseField, StringDatabaseField}
import Storable._

case class ApClassFormat (
  formatId: Int,
  typeId: Int,
  description: String
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
    StringFieldValue(ApClassFormat.fields.description, description)
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
      r.intFields.get("FORMAT_ID") match { case Some(Some(x)) => x; case _ => -1},
      r.intFields.get("TYPE_ID") match { case Some(Some(x)) => x; case _ => -1},
      r.stringFields.get("DESCRIPTION") match { case Some(Some(x)) => x; case _ => "" }
    )

  def getSeedData: Set[ApClassFormat] = Set(
    ApClassFormat(1, 1, ""),
    ApClassFormat(2, 2, ""),
    ApClassFormat(3, 3, "")
  )
}