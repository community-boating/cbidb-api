package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ResetPwReq extends StorableClass(ResetPwReq) {
	object values extends ValuesObject {
		val reqId = new IntFieldValue(self, ResetPwReq.fields.reqId)
		val personUser = new BooleanFIeldValue(self, ResetPwReq.fields.personUser)
		val reqHash = new StringFieldValue(self, ResetPwReq.fields.reqHash)
		val used = new NullableBooleanFIeldValue(self, ResetPwReq.fields.used)
		val createdOn = new NullableLocalDateTimeFieldValue(self, ResetPwReq.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ResetPwReq.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, ResetPwReq.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ResetPwReq.fields.updatedBy)
		val email = new NullableStringFieldValue(self, ResetPwReq.fields.email)
	}
}

object ResetPwReq extends StorableObject[ResetPwReq] {
	val entityName: String = "RESET_PW_REQS"

	object fields extends FieldsObject {
		val reqId = new IntDatabaseField(self, "REQ_ID")
		val personUser = new BooleanDatabaseField(self, "PERSON_USER")
		val reqHash = new StringDatabaseField(self, "REQ_HASH", 50)
		val used = new NullableBooleanDatabaseField(self, "USED")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val email = new NullableStringDatabaseField(self, "EMAIL", 200)
	}

	def primaryKey: IntDatabaseField = fields.reqId
}