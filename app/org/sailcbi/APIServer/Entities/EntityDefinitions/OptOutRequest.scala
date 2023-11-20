package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class OptOutRequest extends StorableClass(OptOutRequest) {
	override object values extends ValuesObject {
		val reqId = new IntFieldValue(self, OptOutRequest.fields.reqId)
		val email = new StringFieldValue(self, OptOutRequest.fields.email)
		val optOutGrpId = new IntFieldValue(self, OptOutRequest.fields.optOutGrpId)
		val doNotSend = new NullableBooleanFieldValue(self, OptOutRequest.fields.doNotSend)
		val token = new StringFieldValue(self, OptOutRequest.fields.token)
		val createdOn = new DateTimeFieldValue(self, OptOutRequest.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, OptOutRequest.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, OptOutRequest.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, OptOutRequest.fields.updatedBy)
	}
}

object OptOutRequest extends StorableObject[OptOutRequest] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "OPT_OUT_REQUESTS"

	object fields extends FieldsObject {
		val reqId = new IntDatabaseField(self, "REQ_ID")
		val email = new StringDatabaseField(self, "EMAIL", 500)
		val optOutGrpId = new IntDatabaseField(self, "OPT_OUT_GRP_ID")
		val doNotSend = new NullableBooleanDatabaseField(self, "DO_NOT_SEND")
		val token = new StringDatabaseField(self, "TOKEN", 50)
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.reqId
}