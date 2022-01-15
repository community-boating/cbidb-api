package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class User extends StorableClass(User) {
	object values extends ValuesObject {
		val userId = new IntFieldValue(self, User.fields.userId)
		val userName = new StringFieldValue(self, User.fields.userName)
		val pwHash = new NullableStringFieldValue(self, User.fields.pwHash)
		val email = new StringFieldValue(self, User.fields.email)
		val createdOn = new NullableLocalDateTimeFieldValue(self, User.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, User.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, User.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, User.fields.updatedBy)
		val locked = new NullableBooleanFIeldValue(self, User.fields.locked)
		val pwChangeReqd = new NullableBooleanFIeldValue(self, User.fields.pwChangeReqd)
		val badAttempts = new NullableDoubleFieldValue(self, User.fields.badAttempts)
		val nameFirst = new NullableStringFieldValue(self, User.fields.nameFirst)
		val nameLast = new NullableStringFieldValue(self, User.fields.nameLast)
		val sagePwCipher = new NullableStringFieldValue(self, User.fields.sagePwCipher)
		val sageToAdd = new NullableStringFieldValue(self, User.fields.sageToAdd)
		val active = new BooleanFIeldValue(self, User.fields.active)
		val hideFromClose = new NullableBooleanFIeldValue(self, User.fields.hideFromClose)
		val pwHashScheme = new NullableStringFieldValue(self, User.fields.pwHashScheme)
		val authNonce = new NullableStringFieldValue(self, User.fields.authNonce)
		val userType = new BooleanFIeldValue(self, User.fields.userType)
	}
}

object User extends StorableObject[User] {
	val entityName: String = "USERS"

	object fields extends FieldsObject {
		val userId = new IntDatabaseField(self, "USER_ID")
		val userName = new StringDatabaseField(self, "USER_NAME", 50)
		val pwHash = new NullableStringDatabaseField(self, "PW_HASH", 500)
		val email = new StringDatabaseField(self, "EMAIL", 100)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val locked = new NullableBooleanDatabaseField(self, "LOCKED")
		val pwChangeReqd = new NullableBooleanDatabaseField(self, "PW_CHANGE_REQD")
		val badAttempts = new NullableDoubleDatabaseField(self, "BAD_ATTEMPTS")
		val nameFirst = new NullableStringDatabaseField(self, "NAME_FIRST", 100)
		val nameLast = new NullableStringDatabaseField(self, "NAME_LAST", 100)
		val sagePwCipher = new NullableStringDatabaseField(self, "SAGE_PW_CIPHER", 4000)
		val sageToAdd = new NullableStringDatabaseField(self, "SAGE_TO_ADD", 4000)
		val active = new BooleanDatabaseField(self, "ACTIVE")
		val hideFromClose = new NullableBooleanDatabaseField(self, "HIDE_FROM_CLOSE")
		val pwHashScheme = new NullableStringDatabaseField(self, "PW_HASH_SCHEME", 20)
		val authNonce = new NullableStringDatabaseField(self, "AUTH_NONCE", 20)
		val userType = new BooleanDatabaseField(self, "USER_TYPE")
	}

	def primaryKey: IntDatabaseField = fields.userId
}