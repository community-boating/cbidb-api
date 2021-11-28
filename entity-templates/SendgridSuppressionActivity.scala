package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class SendgridSuppressionActivity extends StorableClass(SendgridSuppressionActivity) {
	object values extends ValuesObject {
		val rowId = new IntFieldValue(self, SendgridSuppressionActivity.fields.rowId)
		val occurredDatetime = new NullableLocalDateTimeFieldValue(self, SendgridSuppressionActivity.fields.occurredDatetime)
		val email = new NullableStringFieldValue(self, SendgridSuppressionActivity.fields.email)
		val reason = new NullableStringFieldValue(self, SendgridSuppressionActivity.fields.reason)
		val status = new NullableStringFieldValue(self, SendgridSuppressionActivity.fields.status)
		val ip = new NullableStringFieldValue(self, SendgridSuppressionActivity.fields.ip)
		val type = new NullableStringFieldValue(self, SendgridSuppressionActivity.fields.type)
		val createdOn = new NullableLocalDateTimeFieldValue(self, SendgridSuppressionActivity.fields.createdOn)
	}
}

object SendgridSuppressionActivity extends StorableObject[SendgridSuppressionActivity] {
	val entityName: String = "SENDGRID_SUPPRESSION_ACTIVITY"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val occurredDatetime = new NullableLocalDateTimeDatabaseField(self, "OCCURRED_DATETIME")
		val email = new NullableStringDatabaseField(self, "EMAIL", 500)
		val reason = new NullableStringDatabaseField(self, "REASON", 4000)
		val status = new NullableStringDatabaseField(self, "STATUS", 50)
		val ip = new NullableStringDatabaseField(self, "IP", 30)
		val type = new NullableStringDatabaseField(self, "TYPE", 15)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}