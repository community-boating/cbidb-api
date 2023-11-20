package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class SendgridSuppressionActivity extends StorableClass(SendgridSuppressionActivity) {
	override object values extends ValuesObject {
		val rowId = new IntFieldValue(self, SendgridSuppressionActivity.fields.rowId)
		val occurredDatetime = new DateTimeFieldValue(self, SendgridSuppressionActivity.fields.occurredDatetime)
		val email = new StringFieldValue(self, SendgridSuppressionActivity.fields.email)
		val reason = new StringFieldValue(self, SendgridSuppressionActivity.fields.reason)
		val status = new StringFieldValue(self, SendgridSuppressionActivity.fields.status)
		val ip = new NullableStringFieldValue(self, SendgridSuppressionActivity.fields.ip)
		val `type` = new StringFieldValue(self, SendgridSuppressionActivity.fields.`type`)
		val createdOn = new NullableDateTimeFieldValue(self, SendgridSuppressionActivity.fields.createdOn)
	}
}

object SendgridSuppressionActivity extends StorableObject[SendgridSuppressionActivity] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "SENDGRID_SUPPRESSION_ACTIVITY"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val occurredDatetime = new DateTimeDatabaseField(self, "OCCURRED_DATETIME")
		val email = new StringDatabaseField(self, "EMAIL", 500)
		val reason = new StringDatabaseField(self, "REASON", 4000)
		val status = new StringDatabaseField(self, "STATUS", 50)
		val ip = new NullableStringDatabaseField(self, "IP", 30)
		val `type` = new StringDatabaseField(self, "TYPE", 15)
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}