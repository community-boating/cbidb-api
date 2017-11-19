package Entities

import Storable.Fields.FieldValue.IntFieldValue
import Storable.Fields.IntDatabaseField
import Storable._

class Person extends StorableClass {
  this.setCompanion(Person)
  object references extends ReferencesObject {}
  object values extends ValuesObject {
    val personId = new IntFieldValue(self, Person.fields.personId)
  }
}

object Person extends StorableObject[Person] {
  val entityName: String = "PERSONS"

  object fields extends FieldsObject {
    val personId = new IntDatabaseField(self, "PERSON_ID")
  }

  def primaryKey: IntDatabaseField = fields.personId

  def getSeedData: Set[Person] = Set()
}