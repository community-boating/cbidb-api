package Entities

import Storable.Fields.FieldValue.{IntFieldValue, StringFieldValue}
import Storable.Fields.{IntDatabaseField, StringDatabaseField}
import Storable._

class ApClassType extends StorableClass {
  def companion: StorableObject[ApClassType] = ApClassType
  object references extends ReferencesObject {}
  object values extends ValuesObject {
    val typeId = new IntFieldValue(ApClassType.fields.typeId)
    val typeName = new StringFieldValue(ApClassType.fields.typeName)
    val displayOrder = new IntFieldValue(ApClassType.fields.displayOrder)
  }
}

object ApClassType extends StorableObject[ApClassType] {
  val entityName: String = "AP_CLASS_TYPES"

  object fields extends FieldsObject {
    val typeId = new IntDatabaseField(self, "TYPE_ID")
    val typeName = new StringDatabaseField(self, "TYPE_NAME", 50)
    val displayOrder = new IntDatabaseField(self, "DISPLAY_ORDER")
  }

  val primaryKeyName: String = fields.typeId.getFieldName

  def getSeedData: Set[ApClassType] = Set(
    //  ApClassType(1, "Sailing 101", 1),
    //  ApClassType(2, "Sailing 102", 2),
  //   ApClassType(3, "Moar sailing", 3)
  )
}