package Entities

import CbiUtil.SelfInitializable
import Entities.PersonRating.CasePersonRating
import Storable.Fields.FieldValue.IntFieldValue
import Storable.Fields.IntDatabaseField
import Storable._
import Services.ServerStateWrapper.ss

class Person extends StorableClass {
  val instance: Person = this
  this.setCompanion(Person)
  object references extends ReferencesObject {
    var personRatings = new SelfInitializable[Set[PersonRating]](() => ss.pa.pb.getObjectsByFilters(
      PersonRating,
      List(PersonRating.fields.personId.equalsConstant(instance.values.personId.get))
    ).toSet)
  }
  object values extends ValuesObject {
    val personId = new IntFieldValue(self, Person.fields.personId)
  }

  lazy val casePersonRatings: Set[CasePersonRating] = references.personRatings.get.map(_.asCaseClass)

  def hasRatingDirect(ratingId: Int, programId: Int): Boolean = casePersonRatings.contains(CasePersonRating(
    this.values.personId.get,
    ratingId,
    programId
  ))

  def hasRatingSomehow(ratings: Set[Rating], ratingId: Int, programId: Int): Boolean = {
    if (hasRatingDirect(ratingId, programId)) true
    else {
      val subRatings: Set[Rating] = ratings.filter(_.values.overriddenBy.get == Some(ratingId))
      subRatings.map(_.values.ratingId.get).foldLeft(false)((agg, r) => agg || hasRatingSomehow(ratings, r, programId))
    }
  }
}

object Person extends StorableObject[Person] {
  val entityName: String = "PERSONS"

  object fields extends FieldsObject {
    val personId = new IntDatabaseField(self, "PERSON_ID")
  }

  def primaryKey: IntDatabaseField = fields.personId
}