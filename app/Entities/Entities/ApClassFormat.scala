package Entities.Entities

import Storable.Fields.FieldValue.{IntFieldValue, NullableStringFieldValue}
import Storable.Fields.{IntDatabaseField, NullableStringDatabaseField}
import Storable._

class ApClassFormat extends StorableClass {
  this.setCompanion(ApClassFormat)
  object references extends ReferencesObject {
    var apClassType: Option[ApClassType] = None
  }
  object values extends ValuesObject {
    val formatId = new IntFieldValue(self, ApClassFormat.fields.formatId)
    val typeId = new IntFieldValue(self, ApClassFormat.fields.typeId)
    val description = new NullableStringFieldValue(self, ApClassFormat.fields.description)
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
    val description = new NullableStringDatabaseField(self, "DESCRIPTION", 100)
  }

  def primaryKey: IntDatabaseField = fields.formatId
}