package Entities

import CbiUtil.Initializable
import Entities.PersonRating.CasePersonRating
import Services.PersistenceBroker
import Services.ServerStateWrapper.ss
import Storable.Fields.FieldValue.{IntFieldValue, NullableStringFieldValue}
import Storable.Fields.{IntDatabaseField, NullableStringDatabaseField}
import Storable._

class Person extends StorableClass {
  val instance: Person = this
  this.setCompanion(Person)
  object references extends ReferencesObject {
    val personRatings = new Initializable[Set[PersonRating]]
  }
  object values extends ValuesObject {
    val personId = new IntFieldValue(self, Person.fields.personId)
    val nameFirst = new NullableStringFieldValue(self, Person.fields.nameFirst)
    val nameLast = new NullableStringFieldValue(self, Person.fields.nameLast)
    val email = new NullableStringFieldValue(self, Person.fields.email)
  }

  def setPersonRatings(pb: PersistenceBroker): Unit = {
    references.personRatings set pb.getObjectsByFilters(
      PersonRating,
      List(PersonRating.fields.personId.equalsConstant(instance.values.personId.get))
    ).toSet
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
    val nameFirst = new NullableStringDatabaseField(self, "NAME_FIRST", 100)
    val nameLast = new NullableStringDatabaseField(self, "NAME_LAST", 100)
    val email = new NullableStringDatabaseField(self, "EMAIL", 100)
  }

  def primaryKey: IntDatabaseField = fields.personId
}