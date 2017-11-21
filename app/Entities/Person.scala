package Entities

import Entities.PersonRating.CasePersonRating
import Services.PersistenceBroker
import Storable.Fields.FieldValue.IntFieldValue
import Storable.Fields.IntDatabaseField
import Storable._

class Person(implicit pb: PersistenceBroker) extends StorableClass {
  this.setCompanion(Person)
  object references extends ReferencesObject {}
  object values extends ValuesObject {
    val personId = new IntFieldValue(self, Person.fields.personId)
  }

  lazy val ratings: Set[PersonRating] = pb.getObjectsByFilters(
    PersonRating,
    List(PersonRating.fields.personId.equalsConstant(this.values.personId.get))
  ).toSet

  lazy val caseRatings: Set[CasePersonRating] = ratings.map(_.asCaseClass)

  def hasRatingDirect(ratingId: Int, programId: Int): Boolean = caseRatings.contains(CasePersonRating(
    this.values.personId.get,
    ratingId,
    programId
  ))
/*
  def hasRatingSomehow(ratingId: Int, programId: Int): Boolean = {
    if (hasRatingDirect(ratingId, programId)) true
    else {
      val subRatings = pb.
    }
  }*/
}

object Person extends StorableObject[Person] {
  val entityName: String = "PERSONS"

  object fields extends FieldsObject {
    val personId = new IntDatabaseField(self, "PERSON_ID")
  }

  def primaryKey: IntDatabaseField = fields.personId
}