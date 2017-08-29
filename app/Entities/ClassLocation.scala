package Entities

import Storable.Fields.FieldValue.{FieldValue, IntFieldValue, StringFieldValue}
import Storable.Fields.{DatabaseField, IntDatabaseField, StringDatabaseField}
import Storable._

case class ClassLocation(
  locationId: Int,
  locationName: String
) extends StorableClass {
  def companion: StorableObject[ClassLocation] = ClassLocation
  object references extends ReferencesObject {}

  def deconstruct: Set[FieldValue] = Set(
    IntFieldValue(ClassLocation.fields.locationId, locationId),
    StringFieldValue(ClassLocation.fields.locationName, locationName)
  )
}

object ClassLocation extends StorableObject[ClassLocation] {
  val entityName: String = "CLASS_LOCATIONS"

  object fields extends FieldsObject {
    val locationId = new IntDatabaseField(self, "LOCATION_ID")
    val locationName = new StringDatabaseField(self, "LOCATION_NAME", 100)
  }

  val fieldList: List[DatabaseField[_]] = List(
    fields.locationId,
    fields.locationName
  )
  val primaryKeyName: String = fieldList.head.getFieldName

  def construct(r: DatabaseRow): ThisClass =
    new ClassLocation(
      fields.locationId.getValue(r),
      fields.locationName.getValue(r)
    )

  def getSeedData: Set[ClassLocation] = Set()
}