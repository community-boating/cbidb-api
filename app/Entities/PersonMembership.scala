package Entities

import Storable.Fields.FieldValue.{DateFieldValue, IntFieldValue}
import Storable.Fields.{DateDatabaseField, IntDatabaseField}
import Storable._

class PersonMembership extends StorableClass {
  this.setCompanion(ProgramType)
  object references extends ReferencesObject {
    var person: Option[Person] = None
    var membershipType: Option[MembershipType] = None
  }
  object values extends ValuesObject {
    val assignId = new IntFieldValue(self, PersonMembership.fields.assignId)
    val personId = new IntFieldValue(self, PersonMembership.fields.personId)
    val membershipTypeId = new IntFieldValue(self, PersonMembership.fields.membershipTypeId)
    val expirationDate = new DateFieldValue(self, PersonMembership.fields.expirationDate)
  }
}

object PersonMembership extends StorableObject[PersonMembership] {
  val entityName: String = "PERSONS_MEMBERSHIPS"

  object fields extends FieldsObject {
    val assignId = new IntDatabaseField(self, "ASSIGN_ID")
    val personId = new IntDatabaseField(self, "PERSON_ID")
    val membershipTypeId = new IntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
    val expirationDate = new DateDatabaseField(self, "EXPIRATION_DATE")
  }

  def primaryKey: IntDatabaseField = fields.assignId

  def getSeedData: Set[PersonMembership] = Set()
}