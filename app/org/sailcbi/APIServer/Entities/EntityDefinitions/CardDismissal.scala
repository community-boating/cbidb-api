package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class CardDismissal extends StorableClass(CardDismissal) {
	override object values extends ValuesObject {
		val dismissalId = new IntFieldValue(self, CardDismissal.fields.dismissalId)
		val cardAssignId = new IntFieldValue(self, CardDismissal.fields.cardAssignId)
		val season = new DoubleFieldValue(self, CardDismissal.fields.season)
		val createdOn = new DateTimeFieldValue(self, CardDismissal.fields.createdOn)
		val createdBy = new StringFieldValue(self, CardDismissal.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, CardDismissal.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, CardDismissal.fields.updatedBy)
	}
}

object CardDismissal extends StorableObject[CardDismissal] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "CARD_DISMISSALS"

	object fields extends FieldsObject {
		val dismissalId = new IntDatabaseField(self, "DISMISSAL_ID")
		val cardAssignId = new IntDatabaseField(self, "CARD_ASSIGN_ID")
		val season = new DoubleDatabaseField(self, "SEASON")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.dismissalId
}