package Entities.EntityDefinitions

import Storable.Fields.FieldValue.{DateFieldValue, IntFieldValue}
import Storable.Fields.{DateDatabaseField, IntDatabaseField}
import Storable._

class MembershipTypeExp extends StorableClass {
  this.setCompanion(MembershipTypeExp)
  object references extends ReferencesObject {
    var membershipType: Option[MembershipType] = None
  }
  object values extends ValuesObject {
    val expirationId = new IntFieldValue(self, MembershipTypeExp.fields.expirationId)
    val membershipTypeId = new IntFieldValue(self, MembershipTypeExp.fields.membershipTypeId)
    val season = new IntFieldValue(self, MembershipTypeExp.fields.season)
    val expirationDate = new DateFieldValue(self, MembershipTypeExp.fields.expirationDate)
  }
}

object MembershipTypeExp extends StorableObject[MembershipTypeExp] {
  val entityName: String = "MEMBERSHIP_TYPE_EXP"

  object fields extends FieldsObject {
    val expirationId = new IntDatabaseField(self, "EXPIRATION_ID")
    val membershipTypeId = new IntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
    val season = new IntDatabaseField(self, "SEASON")
    val expirationDate = new DateDatabaseField(self, "EXPIRATION_DATE")
  }

  def primaryKey: IntDatabaseField = fields.expirationId

}