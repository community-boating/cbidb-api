package Entities

import Storable.Fields.FieldValue.{BooleanFieldValue, FieldValue, IntFieldValue, StringFieldValue}
import Storable.Fields.{BooleanDatabaseField, DatabaseField, IntDatabaseField, StringDatabaseField}
import Storable._

case class User (
  userId: Int,
  userName: String,
  nameFirst: String,
  nameLast: String,
  active: Boolean,
  hideFromClose: Boolean
) extends StorableClass {
  def companion: StorableObject[User] = User
  object references extends ReferencesObject {}

  def deconstruct: Set[FieldValue] = Set(
    IntFieldValue(User.fields.userId, userId),
    StringFieldValue(User.fields.userName, userName),
    StringFieldValue(User.fields.nameFirst, nameFirst),
    StringFieldValue(User.fields.nameLast, nameLast),
    BooleanFieldValue(User.fields.active, active),
    BooleanFieldValue(User.fields.hideFromClose, hideFromClose)
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
    val hideFromClose = new BooleanDatabaseField(self, "HIDE_FROM_CLOSE")
  }

  val fieldList: List[DatabaseField[_]] = List(
    fields.userId,
    fields.userName,
    fields.nameFirst,
    fields.nameLast,
    fields.active,
    fields.hideFromClose
  )
  val primaryKeyName: String = fieldList.head.getFieldName

  def construct(r: DatabaseRow): ThisClass =
    new User(
      fields.userId.getValue(r),
      fields.userName.getValue(r),
      fields.nameFirst.getValue(r),
      fields.nameLast.getValue(r),
      fields.active.getValue(r),
      fields.hideFromClose.getValue(r)
    )

  def getSeedData: Set[User] = Set(
    User(1, "jcole", "Jon", "Cole", true, false),
    User(2, "gleib", "Ginger", "Leib", true, true),
    User(3, "czechel", "Charlie", "Zechel", true, false),
    User(4, "aalletag", "Andrew", "Alletag", true, false)
  )
}