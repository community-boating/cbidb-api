package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class Pa extends StorableClass(Pa) {
	object values extends ValuesObject {
		val pasId = new IntFieldValue(self, Pa.fields.pasId)
		val userName = new NullableStringFieldValue(self, Pa.fields.userName)
		val pas = new NullableStringFieldValue(self, Pa.fields.pas)
		val expiration = new NullableLocalDateTimeFieldValue(self, Pa.fields.expiration)
		val procName = new NullableStringFieldValue(self, Pa.fields.procName)
		val argString = new NullableStringFieldValue(self, Pa.fields.argString)
	}
}

object Pa extends StorableObject[Pa] {
	val entityName: String = "PAS"

	object fields extends FieldsObject {
		val pasId = new IntDatabaseField(self, "PAS_ID")
		val userName = new NullableStringDatabaseField(self, "USER_NAME", 100)
		val pas = new NullableStringDatabaseField(self, "PAS", 100)
		val expiration = new NullableLocalDateTimeDatabaseField(self, "EXPIRATION")
		val procName = new NullableStringDatabaseField(self, "PROC_NAME", 100)
		val argString = new NullableStringDatabaseField(self, "ARG_STRING", 500)
	}

	def primaryKey: IntDatabaseField = fields.pasId
}