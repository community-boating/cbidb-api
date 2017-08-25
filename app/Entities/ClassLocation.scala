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

  val fieldList: List[DatabaseField] = List(
    fields.locationId,
    fields.locationName
  )
  val primaryKeyName: String = fieldList.head.getFieldName

  def construct(r: DatabaseRow): ThisClass =
    new ClassLocation(
      r.intFields.get("LOCATION_ID") match { case Some(Some(x)) => x; case None => -1},
      r.stringFields.get("LOCATION_NAME") match { case Some(Some(x)) => x; case None => ""}
    )

  def getSeedData: Set[ClassLocation] = Set()
}