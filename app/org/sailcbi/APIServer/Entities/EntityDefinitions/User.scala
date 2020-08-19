package org.sailcbi.APIServer.Entities.EntityDefinitions

import org.sailcbi.APIServer.Storable.FieldValues.{BooleanFieldValue, IntFieldValue, NullableStringFieldValue, StringFieldValue}
import org.sailcbi.APIServer.Storable.Fields.{BooleanDatabaseField, IntDatabaseField, NullableStringDatabaseField, StringDatabaseField}
import org.sailcbi.APIServer.Storable._

class User extends StorableClass {
	this.setCompanion(User)

	object references extends ReferencesObject {}

	object values extends ValuesObject {
		val userId = new IntFieldValue(self, User.fields.userId)
		val userName = new StringFieldValue(self, User.fields.userName)
		val nameFirst = new NullableStringFieldValue(self, User.fields.nameFirst)
		val nameLast = new NullableStringFieldValue(self, User.fields.nameLast)
		val email = new StringFieldValue(self, User.fields.email)
		val active = new BooleanFieldValue(self, User.fields.active)
		val hideFromClose = new BooleanFieldValue(self, User.fields.hideFromClose)
		val pwHash = new NullableStringFieldValue(self, User.fields.pwHash)
	}

	override val valuesList = List(
		values.userId,
		values.userName,
		values.nameFirst,
		values.nameLast,
		values.email,
		values.active,
		values.hideFromClose,
		values.pwHash
	)
}

object User extends StorableObject[User] {
	val entityName: String = "USERS"

	object fields extends FieldsObject {
		val userId = new IntDatabaseField(self, "USER_ID")
		val userName = new StringDatabaseField(self, "USER_NAME", 50)
		val nameFirst = new NullableStringDatabaseField(self, "NAME_FIRST", 100)
		val nameLast = new NullableStringDatabaseField(self, "NAME_LAST", 100)
		val email = new StringDatabaseField(self, "EMAIL", 100)
		val active = new BooleanDatabaseField(self, "ACTIVE")
		val hideFromClose = new BooleanDatabaseField(self, "HIDE_FROM_CLOSE", nullImpliesFalse = true)
		val pwHash = new NullableStringDatabaseField(self, "PW_HASH", 100)
	}

	def primaryKey: IntDatabaseField = fields.userId
}