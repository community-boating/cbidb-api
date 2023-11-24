package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class User extends StorableClass(User) {
	override object references extends ReferencesObject {
		val extraRoles = new Initializable[List[UserRole]]
	}

	override object values extends ValuesObject {
		val userId = new IntFieldValue(self, User.fields.userId)
		val userName = new StringFieldValue(self, User.fields.userName)
		val pwHash = new StringFieldValue(self, User.fields.pwHash)
		val email = new StringFieldValue(self, User.fields.email)
		val createdOn = new DateTimeFieldValue(self, User.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, User.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, User.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, User.fields.updatedBy)
		val locked = new NullableBooleanFieldValue(self, User.fields.locked)
		val pwChangeReqd = new NullableBooleanFieldValue(self, User.fields.pwChangeReqd)
		val badAttempts = new NullableDoubleFieldValue(self, User.fields.badAttempts)
		val nameFirst = new StringFieldValue(self, User.fields.nameFirst)
		val nameLast = new StringFieldValue(self, User.fields.nameLast)
		val sagePwCipher = new NullableStringFieldValue(self, User.fields.sagePwCipher)
		val sageToAdd = new NullableStringFieldValue(self, User.fields.sageToAdd)
		val active = new BooleanFieldValue(self, User.fields.active)
		val hideFromClose = new NullableBooleanFieldValue(self, User.fields.hideFromClose)
		val pwHashScheme = new NullableStringFieldValue(self, User.fields.pwHashScheme)
		val authNonce = new NullableStringFieldValue(self, User.fields.authNonce)
		val userType = new NullableStringFieldValue(self, User.fields.userType)
		val accessProfileId = new IntFieldValue(self, User.fields.accessProfileId)
		val noAppAccess = new NullableStringFieldValue(self, User.fields.noAppAccess)
	}
}

object User extends StorableObject[User] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "USERS"

	object fields extends FieldsObject {
		val userId = new IntDatabaseField(self, "USER_ID")
		val userName = new StringDatabaseField(self, "USER_NAME", 50)
		@NullableInDatabase
		val pwHash = new StringDatabaseField(self, "PW_HASH", 500)
		val email = new StringDatabaseField(self, "EMAIL", 100)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val locked = new NullableBooleanDatabaseField(self, "LOCKED")
		val pwChangeReqd = new NullableBooleanDatabaseField(self, "PW_CHANGE_REQD")
		val badAttempts = new NullableDoubleDatabaseField(self, "BAD_ATTEMPTS")
		@NullableInDatabase
		val nameFirst = new StringDatabaseField(self, "NAME_FIRST", 100)
		@NullableInDatabase
		val nameLast = new StringDatabaseField(self, "NAME_LAST", 100)
		val sagePwCipher = new NullableStringDatabaseField(self, "SAGE_PW_CIPHER", 4000)
		val sageToAdd = new NullableStringDatabaseField(self, "SAGE_TO_ADD", 4000)
		val active = new BooleanDatabaseField(self, "ACTIVE", false)
		val hideFromClose = new NullableBooleanDatabaseField(self, "HIDE_FROM_CLOSE")
		val pwHashScheme = new NullableStringDatabaseField(self, "PW_HASH_SCHEME", 20)
		val authNonce = new NullableStringDatabaseField(self, "AUTH_NONCE", 20)
		val userType = new NullableStringDatabaseField(self, "USER_TYPE", 1)
		val accessProfileId = new IntDatabaseField(self, "ACCESS_PROFILE_ID")
		val noAppAccess = new NullableStringDatabaseField(self, "NO_APP_ACCESS", 1)
	}

	def primaryKey: IntDatabaseField = fields.userId
}