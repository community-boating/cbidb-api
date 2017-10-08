package Entities

import Storable.Fields.FieldValue.{BooleanFieldValue, IntFieldValue, StringFieldValue}
import Storable.Fields.{BooleanDatabaseField, IntDatabaseField, StringDatabaseField}
import Storable._

class User extends StorableClass {
  object references extends ReferencesObject {}
  object values extends ValuesObject {
    val userId = new IntFieldValue(self, User.fields.userId)
    val userName = new StringFieldValue(self, User.fields.userName)
    val nameFirst = new StringFieldValue(self, User.fields.nameFirst)
    val nameLast = new StringFieldValue(self, User.fields.nameLast)
    val active = new BooleanFieldValue(self, User.fields.active)
    val hideFromClose = new BooleanFieldValue(self, User.fields.hideFromClose)
  }
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

  val primaryKey: IntDatabaseField = fields.userId

  def getSeedData: Set[User] = Set(
  //  User(1, "jcole", "Jon", "Cole", true, false),
  //  User(2, "gleib", "Ginger", "Leib", true, true),
  //  User(3, "czechel", "Charlie", "Zechel", true, false),
  //  User(4, "aalletag", "Andrew", "Alletag", true, false)
  )
}