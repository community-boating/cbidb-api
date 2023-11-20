package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ResetPwReq extends StorableClass(ResetPwReq) {
	override object values extends ValuesObject {
		val reqId = new IntFieldValue(self, ResetPwReq.fields.reqId)
		val personUser = new StringFieldValue(self, ResetPwReq.fields.personUser)
		val reqHash = new StringFieldValue(self, ResetPwReq.fields.reqHash)
		val used = new NullableStringFieldValue(self, ResetPwReq.fields.used)
		val createdOn = new DateTimeFieldValue(self, ResetPwReq.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ResetPwReq.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, ResetPwReq.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ResetPwReq.fields.updatedBy)
		val email = new StringFieldValue(self, ResetPwReq.fields.email)
	}
}

object ResetPwReq extends StorableObject[ResetPwReq] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "RESET_PW_REQS"

	object fields extends FieldsObject {
		val reqId = new IntDatabaseField(self, "REQ_ID")
		val personUser = new StringDatabaseField(self, "PERSON_USER", 1)
		val reqHash = new StringDatabaseField(self, "REQ_HASH", 50)
		val used = new NullableStringDatabaseField(self, "USED", 1)
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val email = new StringDatabaseField(self, "EMAIL", 200)
	}

	def primaryKey: IntDatabaseField = fields.reqId
}