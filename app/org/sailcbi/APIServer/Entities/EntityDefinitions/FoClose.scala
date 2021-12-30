package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FoClose extends StorableClass(FoClose) {
	object values extends ValuesObject {
		val closeId = new IntFieldValue(self, FoClose.fields.closeId)
		val closedDatetime = new NullableDateTimeFieldValue(self, FoClose.fields.closedDatetime)
//		val createdOn = new NullableLocalDateTimeFieldValue(self, FoClose.fields.createdOn)
//		val createdBy = new NullableStringFieldValue(self, FoClose.fields.createdBy)
//		val updatedOn = new NullableLocalDateTimeFieldValue(self, FoClose.fields.updatedOn)
//		val updatedBy = new NullableStringFieldValue(self, FoClose.fields.updatedBy)
		val tapeValue = new NullableDoubleFieldValue(self, FoClose.fields.tapeValue)
		val notes = new NullableClobFieldValue(self, FoClose.fields.notes)
		val finalizedBy = new NullableDoubleFieldValue(self, FoClose.fields.finalizedBy)
		val closeAlias = new NullableStringFieldValue(self, FoClose.fields.closeAlias)
		val tallyValueAtClose = new NullableDoubleFieldValue(self, FoClose.fields.tallyValueAtClose)
		val stripeTallyAtClose = new NullableDoubleFieldValue(self, FoClose.fields.stripeTallyAtClose)
	}
}

object FoClose extends StorableObject[FoClose] {
	val entityName: String = "FO_CLOSES"

	object fields extends FieldsObject {
		val closeId = new IntDatabaseField(self, "CLOSE_ID")
		val closedDatetime = new NullableDateTimeDatabaseField(self, "CLOSED_DATETIME")
//		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
//		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
//		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
//		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val tapeValue = new NullableDoubleDatabaseField(self, "TAPE_VALUE")
		val notes = new NullableClobDatabaseField(self, "NOTES")
		val finalizedBy = new NullableDoubleDatabaseField(self, "FINALIZED_BY")
		val closeAlias = new NullableStringDatabaseField(self, "CLOSE_ALIAS", 100)
		val tallyValueAtClose = new NullableDoubleDatabaseField(self, "TALLY_VALUE_AT_CLOSE")
		val stripeTallyAtClose = new NullableDoubleDatabaseField(self, "STRIPE_TALLY_AT_CLOSE")
	}

	def primaryKey: IntDatabaseField = fields.closeId
}