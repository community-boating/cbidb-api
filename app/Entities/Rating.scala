package Entities

import Storable.Fields.FieldValue.{IntFieldValue, StringFieldValue}
import Storable.Fields.{IntDatabaseField, StringDatabaseField}
import Storable._

class Rating extends StorableClass {
  this.setCompanion(Rating)
  object references extends ReferencesObject {}
  object values extends ValuesObject {
    val ratingId = new IntFieldValue(self, Rating.fields.ratingId)
    val ratingName = new StringFieldValue(self, Rating.fields.ratingName)
  }
}

object Rating extends StorableObject[Rating] {
  val entityName: String = "RATINGS"

  object fields extends FieldsObject {
    val ratingId = new IntDatabaseField(self, "RATING_ID")
    val ratingName = new StringDatabaseField(self, "RATING_NAME", 100)
  }

  def primaryKey: IntDatabaseField = fields.ratingId

  def getSeedData: Set[Rating] = Set()
}