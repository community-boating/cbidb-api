package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class UsersIp extends StorableClass(UsersIp) {
	object values extends ValuesObject {
		val userId = new IntFieldValue(self, UsersIp.fields.userId)
		val ipAddress = new StringFieldValue(self, UsersIp.fields.ipAddress)
		val firstDatetime = new NullableLocalDateTimeFieldValue(self, UsersIp.fields.firstDatetime)
		val lastDatetime = new NullableLocalDateTimeFieldValue(self, UsersIp.fields.lastDatetime)
		val useCount = new NullableDoubleFieldValue(self, UsersIp.fields.useCount)
	}
}

object UsersIp extends StorableObject[UsersIp] {
	val entityName: String = "USERS_IPS"

	object fields extends FieldsObject {
		val userId = new IntDatabaseField(self, "USER_ID")
		val ipAddress = new StringDatabaseField(self, "IP_ADDRESS", 50)
		val firstDatetime = new NullableLocalDateTimeDatabaseField(self, "FIRST_DATETIME")
		val lastDatetime = new NullableLocalDateTimeDatabaseField(self, "LAST_DATETIME")
		val useCount = new NullableDoubleDatabaseField(self, "USE_COUNT")
	}

	def primaryKey: IntDatabaseField = fields.userId
}