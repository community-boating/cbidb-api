package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FoClose extends StorableClass(FoClose) {
	override object values extends ValuesObject {
		val closeId = new IntFieldValue(self, FoClose.fields.closeId)
		val closedDatetime = new NullableDateTimeFieldValue(self, FoClose.fields.closedDatetime)
		val createdOn = new DateTimeFieldValue(self, FoClose.fields.createdOn)
		val createdBy = new StringFieldValue(self, FoClose.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, FoClose.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, FoClose.fields.updatedBy)
		val batchNet = new NullableDoubleFieldValue(self, FoClose.fields.batchNet)
		val batchNum = new NullableDoubleFieldValue(self, FoClose.fields.batchNum)
		val itemCt = new NullableDoubleFieldValue(self, FoClose.fields.itemCt)
		val settleRef = new NullableStringFieldValue(self, FoClose.fields.settleRef)
		val closed = new NullableBooleanFieldValue(self, FoClose.fields.closed)
		val tapeValue = new NullableDoubleFieldValue(self, FoClose.fields.tapeValue)
		val notes = new NullableStringFieldValue(self, FoClose.fields.notes)
		val finalizedBy = new NullableDoubleFieldValue(self, FoClose.fields.finalizedBy)
		val closeAlias = new NullableStringFieldValue(self, FoClose.fields.closeAlias)
		val tallyValueAtClose = new NullableDoubleFieldValue(self, FoClose.fields.tallyValueAtClose)
		val nextSettleReminderSent = new NullableDateTimeFieldValue(self, FoClose.fields.nextSettleReminderSent)
		val settledDatetime = new NullableDateTimeFieldValue(self, FoClose.fields.settledDatetime)
		val stripeTallyAtClose = new NullableDoubleFieldValue(self, FoClose.fields.stripeTallyAtClose)
	}
}

object FoClose extends StorableObject[FoClose] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "FO_CLOSES"

	object fields extends FieldsObject {
		val closeId = new IntDatabaseField(self, "CLOSE_ID")
		val closedDatetime = new NullableDateTimeDatabaseField(self, "CLOSED_DATETIME")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val batchNet = new NullableDoubleDatabaseField(self, "BATCH_NET")
		val batchNum = new NullableDoubleDatabaseField(self, "BATCH_NUM")
		val itemCt = new NullableDoubleDatabaseField(self, "ITEM_CT")
		val settleRef = new NullableStringDatabaseField(self, "SETTLE_REF", 20)
		val closed = new NullableBooleanDatabaseField(self, "CLOSED")
		val tapeValue = new NullableDoubleDatabaseField(self, "TAPE_VALUE")
		val notes = new NullableStringDatabaseField(self, "NOTES", -1)
		val finalizedBy = new NullableDoubleDatabaseField(self, "FINALIZED_BY")
		val closeAlias = new NullableStringDatabaseField(self, "CLOSE_ALIAS", 100)
		val tallyValueAtClose = new NullableDoubleDatabaseField(self, "TALLY_VALUE_AT_CLOSE")
		val nextSettleReminderSent = new NullableDateTimeDatabaseField(self, "NEXT_SETTLE_REMINDER_SENT")
		val settledDatetime = new NullableDateTimeDatabaseField(self, "SETTLED_DATETIME")
		val stripeTallyAtClose = new NullableDoubleDatabaseField(self, "STRIPE_TALLY_AT_CLOSE")
	}

	def primaryKey: IntDatabaseField = fields.closeId
}