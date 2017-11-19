package Entities

import Storable.Fields.FieldValue.{IntFieldValue, StringFieldValue}
import Storable.Fields.{IntDatabaseField, StringDatabaseField}
import Storable._

class MembershipType extends StorableClass {
  this.setCompanion(MembershipType)
  object references extends ReferencesObject {
    var program: Option[ProgramType] = None
  }
  object values extends ValuesObject {
    val membershipTypeId = new IntFieldValue(self, MembershipType.fields.membershipTypeId)
    val membershipTypeName = new StringFieldValue(self, MembershipType.fields.membershipTypeName)
    val programId = new IntFieldValue(self, MembershipType.fields.programId)
  }
}

object MembershipType extends StorableObject[MembershipType] {
  val entityName: String = "MEMBERSHIP_TYPES"

  object fields extends FieldsObject {
    val membershipTypeId = new IntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
    val membershipTypeName = new StringDatabaseField(self, "MEMBERSHIP_TYPE_NAME", 100)
    val programId = new IntDatabaseField(self, "PROGRAM_ID")
  }

  def primaryKey: IntDatabaseField = fields.membershipTypeId

  def getSeedData: Set[MembershipType] = Set()
}