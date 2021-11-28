package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class CardDismissal extends StorableClass(CardDismissal) {
	object values extends ValuesObject {
		val dismissalId = new IntFieldValue(self, CardDismissal.fields.dismissalId)
		val cardAssignId = new NullableIntFieldValue(self, CardDismissal.fields.cardAssignId)
		val season = new NullableDoubleFieldValue(self, CardDismissal.fields.season)
		val createdOn = new NullableLocalDateTimeFieldValue(self, CardDismissal.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, CardDismissal.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, CardDismissal.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, CardDismissal.fields.updatedBy)
	}
}

object CardDismissal extends StorableObject[CardDismissal] {
	val entityName: String = "CARD_DISMISSALS"

	object fields extends FieldsObject {
		val dismissalId = new IntDatabaseField(self, "DISMISSAL_ID")
		val cardAssignId = new NullableIntDatabaseField(self, "CARD_ASSIGN_ID")
		val season = new NullableDoubleDatabaseField(self, "SEASON")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.dismissalId
}