package Entities

import Storable.Fields.{DatabaseField, IntDatabaseField, StringDatabaseField}
import Storable._

case class ClassLocation(
  locationId: Int,
  locationName: String
) extends StorableClass {
  object references extends ReferencesObject {}
}

object ClassLocation extends StorableObject[ClassLocation] {
  val entityName: String = "CLASS_LOCATIONS"

  object fields extends FieldsObject {
    val locationId = new IntDatabaseField(self, "LOCATION_ID")
    val locationName = new StringDatabaseField(self, "LOCATION_NAME")
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
}