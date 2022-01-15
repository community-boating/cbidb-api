package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class OptOutRequest extends StorableClass(OptOutRequest) {
	object values extends ValuesObject {
		val reqId = new IntFieldValue(self, OptOutRequest.fields.reqId)
		val email = new NullableStringFieldValue(self, OptOutRequest.fields.email)
		val optOutGrpId = new NullableIntFieldValue(self, OptOutRequest.fields.optOutGrpId)
		val doNotSend = new NullableBooleanFIeldValue(self, OptOutRequest.fields.doNotSend)
		val token = new NullableStringFieldValue(self, OptOutRequest.fields.token)
		val createdOn = new NullableLocalDateTimeFieldValue(self, OptOutRequest.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, OptOutRequest.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, OptOutRequest.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, OptOutRequest.fields.updatedBy)
	}
}

object OptOutRequest extends StorableObject[OptOutRequest] {
	val entityName: String = "OPT_OUT_REQUESTS"

	object fields extends FieldsObject {
		val reqId = new IntDatabaseField(self, "REQ_ID")
		val email = new NullableStringDatabaseField(self, "EMAIL", 500)
		val optOutGrpId = new NullableIntDatabaseField(self, "OPT_OUT_GRP_ID")
		val doNotSend = new NullableBooleanDatabaseField(self, "DO_NOT_SEND")
		val token = new NullableStringDatabaseField(self, "TOKEN", 50)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.reqId
}