package Entities

import Storable.Fields.FieldValue.IntFieldValue
import Storable.Fields.IntDatabaseField
import Storable._

class PersonRating extends StorableClass {
  this.setCompanion(PersonRating)
  object references extends ReferencesObject {
    var person: Option[Person] = None
    var rating: Option[Rating] = None
    var program: Option[ProgramType] = None
  }
  object values extends ValuesObject {
    val assignId = new IntFieldValue(self, PersonRating.fields.assignId)
    val personId = new IntFieldValue(self, PersonRating.fields.personId)
    val ratingId = new IntFieldValue(self, PersonRating.fields.ratingId)
    val programId = new IntFieldValue(self, PersonRating.fields.programId)
  }
}

object PersonRating extends StorableObject[PersonRating] {
  val entityName: String = "PERSONS_RATINGS"

  object fields extends FieldsObject {
    val assignId = new IntDatabaseField(self, "ASSIGN_ID")
    val personId = new IntDatabaseField(self, "PERSON_ID")
    val ratingId = new IntDatabaseField(self, "RATING_ID")
    val programId = new IntDatabaseField(self, "PROGRAM_ID")
  }

  def primaryKey: IntDatabaseField = fields.assignId

  def getSeedData: Set[PersonRating] = Set()
}