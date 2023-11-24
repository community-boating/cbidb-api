package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class UsersIp extends StorableClass(UsersIp) {
	override object values extends ValuesObject {
		val userId = new IntFieldValue(self, UsersIp.fields.userId)
		val ipAddress = new StringFieldValue(self, UsersIp.fields.ipAddress)
		val firstDatetime = new NullableDateTimeFieldValue(self, UsersIp.fields.firstDatetime)
		val lastDatetime = new NullableDateTimeFieldValue(self, UsersIp.fields.lastDatetime)
		val useCount = new NullableDoubleFieldValue(self, UsersIp.fields.useCount)
	}
}

object UsersIp extends StorableObject[UsersIp] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "USERS_IPS"

	object fields extends FieldsObject {
		val userId = new IntDatabaseField(self, "USER_ID")
		val ipAddress = new StringDatabaseField(self, "IP_ADDRESS", 50)
		val firstDatetime = new NullableDateTimeDatabaseField(self, "FIRST_DATETIME")
		val lastDatetime = new NullableDateTimeDatabaseField(self, "LAST_DATETIME")
		val useCount = new NullableDoubleDatabaseField(self, "USE_COUNT")
	}

	def primaryKey: IntDatabaseField = fields.userId
}