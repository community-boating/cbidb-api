package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FoClose extends StorableClass(FoClose) {
	object values extends ValuesObject {
		val closeId = new IntFieldValue(self, FoClose.fields.closeId)
		val closedDatetime = new NullableLocalDateTimeFieldValue(self, FoClose.fields.closedDatetime)
		val createdOn = new NullableLocalDateTimeFieldValue(self, FoClose.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, FoClose.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, FoClose.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, FoClose.fields.updatedBy)
		val batchNet = new NullableDoubleFieldValue(self, FoClose.fields.batchNet)
		val batchNum = new NullableDoubleFieldValue(self, FoClose.fields.batchNum)
		val itemCt = new NullableDoubleFieldValue(self, FoClose.fields.itemCt)
		val settleRef = new NullableStringFieldValue(self, FoClose.fields.settleRef)
		val closed = new NullableBooleanFIeldValue(self, FoClose.fields.closed)
		val tapeValue = new NullableDoubleFieldValue(self, FoClose.fields.tapeValue)
		val notes = new NullableUnknownFieldType(self, FoClose.fields.notes)
		val finalizedBy = new NullableDoubleFieldValue(self, FoClose.fields.finalizedBy)
		val closeAlias = new NullableStringFieldValue(self, FoClose.fields.closeAlias)
		val tallyValueAtClose = new NullableDoubleFieldValue(self, FoClose.fields.tallyValueAtClose)
		val settledDatetime = new NullableLocalDateTimeFieldValue(self, FoClose.fields.settledDatetime)
		val nextSettleReminderSent = new NullableLocalDateTimeFieldValue(self, FoClose.fields.nextSettleReminderSent)
		val stripeTallyAtClose = new NullableDoubleFieldValue(self, FoClose.fields.stripeTallyAtClose)
	}
}

object FoClose extends StorableObject[FoClose] {
	val entityName: String = "FO_CLOSES"

	object fields extends FieldsObject {
		val closeId = new IntDatabaseField(self, "CLOSE_ID")
		val closedDatetime = new NullableLocalDateTimeDatabaseField(self, "CLOSED_DATETIME")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val batchNet = new NullableDoubleDatabaseField(self, "BATCH_NET")
		val batchNum = new NullableDoubleDatabaseField(self, "BATCH_NUM")
		val itemCt = new NullableDoubleDatabaseField(self, "ITEM_CT")
		val settleRef = new NullableStringDatabaseField(self, "SETTLE_REF", 20)
		val closed = new NullableBooleanDatabaseField(self, "CLOSED")
		val tapeValue = new NullableDoubleDatabaseField(self, "TAPE_VALUE")
		val notes = new NullableUnknownFieldType(self, "NOTES")
		val finalizedBy = new NullableDoubleDatabaseField(self, "FINALIZED_BY")
		val closeAlias = new NullableStringDatabaseField(self, "CLOSE_ALIAS", 100)
		val tallyValueAtClose = new NullableDoubleDatabaseField(self, "TALLY_VALUE_AT_CLOSE")
		val settledDatetime = new NullableLocalDateTimeDatabaseField(self, "SETTLED_DATETIME")
		val nextSettleReminderSent = new NullableLocalDateTimeDatabaseField(self, "NEXT_SETTLE_REMINDER_SENT")
		val stripeTallyAtClose = new NullableDoubleDatabaseField(self, "STRIPE_TALLY_AT_CLOSE")
	}

	def primaryKey: IntDatabaseField = fields.closeId
}