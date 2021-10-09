package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues.{BooleanFieldValue, IntFieldValue, NullableStringFieldValue, StringFieldValue}
import com.coleji.neptune.Storable.Fields.{BooleanDatabaseField, IntDatabaseField, NullableStringDatabaseField, StringDatabaseField}
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.UserTypes.StaffRequestCache

class User extends StorableClass(User) {
	object values extends ValuesObject {
		val userId = new IntFieldValue(self, User.fields.userId)
		val userName = new StringFieldValue(self, User.fields.userName)
		val nameFirst = new NullableStringFieldValue(self, User.fields.nameFirst)
		val nameLast = new NullableStringFieldValue(self, User.fields.nameLast)
		val email = new StringFieldValue(self, User.fields.email)
		val active = new BooleanFieldValue(self, User.fields.active)
		val hideFromClose = new BooleanFieldValue(self, User.fields.hideFromClose)
		val pwHash = new NullableStringFieldValue(self, User.fields.pwHash)
		val locked = new BooleanFieldValue(self, User.fields.locked)
		val pwChangeRequired = new BooleanFieldValue(self, User.fields.pwChangeRequired)
		val pwHashScheme = new NullableStringFieldValue(self, User.fields.pwHashScheme)
		val userType = new StringFieldValue(self, User.fields.userType)
	}

	override def toString: String = this.valuesList.filter(_.getPersistenceFieldName != "PW_HASH").toString()

	def canManage(otherUser: User): Boolean = {
		val myTypeLevel = User.userTypeLevel(this.values.userType.get)
		val othersTypeLevel = User.userTypeLevel(otherUser.values.userType.get)
		myTypeLevel > othersTypeLevel
	}
}

object User extends StorableObject[User] {
	override val entityName: String = "USERS"

	object USER_TYPES {
		val USER = "U"
		val MANAGER = "M"
		val ADMIN = "A"
	}

	def userTypeLevel(userType: String): Int = userType match {
		case USER_TYPES.USER => 1
		case USER_TYPES.MANAGER => 2
		case USER_TYPES.ADMIN => 3
	}

	object fields extends FieldsObject {
		val userId = new IntDatabaseField(self, "USER_ID")
		val userName = new StringDatabaseField(self, "USER_NAME", 50)
		val nameFirst = new NullableStringDatabaseField(self, "NAME_FIRST", 100)
		val nameLast = new NullableStringDatabaseField(self, "NAME_LAST", 100)
		val email = new StringDatabaseField(self, "EMAIL", 100)
		val active = new BooleanDatabaseField(self, "ACTIVE")
		val hideFromClose = new BooleanDatabaseField(self, "HIDE_FROM_CLOSE", nullImpliesFalse = true)
		val pwHash = new NullableStringDatabaseField(self, "PW_HASH", 100)
		val locked = new BooleanDatabaseField(self, "LOCKED", nullImpliesFalse = true)
		val pwChangeRequired = new BooleanDatabaseField(self, "PW_CHANGE_REQD", nullImpliesFalse = true)
		val pwHashScheme = new NullableStringDatabaseField(self, "PW_HASH_SCHEME", 20)
		val userType = new StringDatabaseField(self, "USER_TYPE", 1)
	}

	def getAuthedUser(rc: StaffRequestCache): User = {
		rc.getObjectsByFilters(User, List(User.fields.userName.equalsConstantLowercase(rc.userName))).head
	}

	override def primaryKey: IntDatabaseField = fields.userId
}