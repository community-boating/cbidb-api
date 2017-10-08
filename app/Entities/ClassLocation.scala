package Entities

import Storable.Fields.FieldValue.{IntFieldValue, StringFieldValue}
import Storable.Fields.{IntDatabaseField, StringDatabaseField}
import Storable._

class ClassLocation extends StorableClass {
  object references extends ReferencesObject {}
  object values extends ValuesObject {
    val locationId = new IntFieldValue(ClassLocation.fields.locationId)
    val locationName = new StringFieldValue(ClassLocation.fields.locationName)
  }
}

object ClassLocation extends StorableObject[ClassLocation] {
  val entityName: String = "CLASS_LOCATIONS"

  object fields extends FieldsObject {
    val locationId = new IntDatabaseField(self, "LOCATION_ID")
    val locationName = new StringDatabaseField(self, "LOCATION_NAME", 100)
  }

  val primaryKey: IntDatabaseField = fields.locationId

  def getSeedData: Set[ClassLocation] = Set()
}