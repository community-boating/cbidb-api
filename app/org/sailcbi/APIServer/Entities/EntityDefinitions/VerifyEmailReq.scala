package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class VerifyEmailReq extends StorableClass(VerifyEmailReq) {
	override object values extends ValuesObject {
		val reqId = new IntFieldValue(self, VerifyEmailReq.fields.reqId)
		val email = new StringFieldValue(self, VerifyEmailReq.fields.email)
		val token = new StringFieldValue(self, VerifyEmailReq.fields.token)
		val reqDatetime = new DateTimeFieldValue(self, VerifyEmailReq.fields.reqDatetime)
		val personId = new IntFieldValue(self, VerifyEmailReq.fields.personId)
		val isValid = new NullableBooleanFieldValue(self, VerifyEmailReq.fields.isValid)
	}
}

object VerifyEmailReq extends StorableObject[VerifyEmailReq] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "VERIFY_EMAIL_REQS"

	object fields extends FieldsObject {
		val reqId = new IntDatabaseField(self, "REQ_ID")
		val email = new StringDatabaseField(self, "EMAIL", 1000)
		val token = new StringDatabaseField(self, "TOKEN", 50)
		val reqDatetime = new DateTimeDatabaseField(self, "REQ_DATETIME")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val isValid = new NullableBooleanDatabaseField(self, "IS_VALID")
	}

	def primaryKey: IntDatabaseField = fields.reqId
}