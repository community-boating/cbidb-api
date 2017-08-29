package Entities

import Storable.Fields.FieldValue.{BooleanFieldValue, FieldValue, IntFieldValue, StringFieldValue}
import Storable.Fields.{BooleanDatabaseField, DatabaseField, IntDatabaseField, StringDatabaseField}
import Storable._

case class User (
  userId: Int,
  userName: String,
  nameFirst: String,
  nameLast: String,
  active: Boolean
) extends StorableClass {
  def companion: StorableObject[User] = User
  object references extends ReferencesObject {}

  def deconstruct: Set[FieldValue] = Set(
    IntFieldValue(User.fields.userId, userId),
    StringFieldValue(User.fields.userName, userName),
    StringFieldValue(User.fields.nameFirst, nameFirst),
    StringFieldValue(User.fields.nameLast, nameLast),
    BooleanFieldValue(User.fields.active, active)
  )
}

object User extends StorableObject[User] {
  val entityName: String = "USERS"

  object fields extends FieldsObject {
    val userId = new IntDatabaseField(self, "USER_ID")
    val userName = new StringDatabaseField(self, "USER_NAME", 50)
    val nameFirst = new StringDatabaseField(self, "NAME_FIRST", 100)
    val nameLast = new StringDatabaseField(self, "NAME_LAST", 100)
    val active = new BooleanDatabaseField(self, "ACTIVE")
  }

  val fieldList: List[DatabaseField] = List(
    fields.userId,
    fields.userName,
    fields.nameFirst,
    fields.nameLast,
    fields.active
  )
  val primaryKeyName: String = fieldList.head.getFieldName

  def construct(r: DatabaseRow): ThisClass =
    new User(
      r.intFields.get("USER_ID") match { case Some(Some(x)) => x; case _ => -1},
      r.stringFields.get("USER_NAME") match { case Some(Some(x)) => x; case _ => "" },
      r.stringFields.get("NAME_FIRST") match { case Some(Some(x)) => x; case _ => "" },
      r.stringFields.get("NAME_LAST") match { case Some(Some(x)) => x; case _ => "" },
      r.stringFields.get("ACTIVE") match { case Some(Some("Y")) => true; case _ => false }
    )

  def getSeedData: Set[User] = Set(
    User(1, "jcole", "Jon", "Cole", true),
    User(2, "gleib", "Ginger", "Leib", false),
    User(3, "czechel", "Charlie", "Zechel", true),
    User(4, "aalletag", "Andrew", "Alletag", true)
  )
}