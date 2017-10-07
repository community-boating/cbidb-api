package Entities

import Storable.Fields.FieldValue.{IntFieldValue, StringFieldValue}
import Storable.Fields.{IntDatabaseField, StringDatabaseField}
import Storable._

class ApClassFormat extends StorableClass {
  def companion: StorableObject[ApClassFormat] = ApClassFormat

  object references extends ReferencesObject {
    var apClassType: Option[ApClassType] = None
  }
  object values extends ValuesObject {
    val formatId = new IntFieldValue(ApClassFormat.fields.formatId)
    val typeId = new IntFieldValue(ApClassFormat.fields.typeId)
    val description = new StringFieldValue(ApClassFormat.fields.description)
  }

  def setApClassType(v: ApClassType): Unit = references.apClassType = Some(v)

  def getApClassType: ApClassType = references.apClassType match {
    case Some(x) => x
    case None => throw new Exception("ApClassType unset for ApClassFormat " + values.formatId.get)
  }
}

object ApClassFormat extends StorableObject[ApClassFormat] {
  val entityName: String = "AP_CLASS_FORMATS"

  object fields extends FieldsObject {
    val formatId = new IntDatabaseField(self, "FORMAT_ID")
    val typeId = new IntDatabaseField(self, "TYPE_ID")
    val description = new StringDatabaseField(self, "DESCRIPTION", 100)
  }

  val primaryKeyName: String = fields.formatId.getFieldName

  def getSeedData: Set[ApClassFormat] = Set(
  //  ApClassFormat(1, 1, None),
  //  ApClassFormat(2, 2, None),
  //  ApClassFormat(3, 3, None)
  )
}