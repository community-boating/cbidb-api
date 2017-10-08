package Entities

import Storable.Fields.FieldValue.{IntFieldValue, StringFieldValue}
import Storable.Fields.{IntDatabaseField, StringDatabaseField}
import Storable._

class JpClassType extends StorableClass {
  this.setCompanion(JpClassType)
  object references extends ReferencesObject {}
  object values extends ValuesObject {
    val typeId = new IntFieldValue(self, JpClassType.fields.typeId)
    val typeName = new StringFieldValue(self, JpClassType.fields.typeName)
    val displayOrder = new IntFieldValue(self, JpClassType.fields.displayOrder)
  }
}

object JpClassType extends StorableObject[JpClassType] {
  val entityName: String = "JP_CLASS_TYPES"

  object fields extends FieldsObject {
    val typeId = new IntDatabaseField(self, "TYPE_ID")
    val typeName = new StringDatabaseField(self, "TYPE_NAME", 50)
    val displayOrder = new IntDatabaseField(self, "DISPLAY_ORDER")
  }

  val primaryKey: IntDatabaseField = fields.typeId

  def getSeedData: Set[JpClassType] = Set()
}