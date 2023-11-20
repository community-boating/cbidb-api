package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class RaCz extends StorableClass(RaCz) {
	override object values extends ValuesObject {
		val eventId = new IntFieldValue(self, RaCz.fields.eventId)
		val signoutId = new IntFieldValue(self, RaCz.fields.signoutId)
		val eventDatetime = new DateTimeFieldValue(self, RaCz.fields.eventDatetime)
		val createdOn = new DateTimeFieldValue(self, RaCz.fields.createdOn)
		val createdBy = new StringFieldValue(self, RaCz.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, RaCz.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, RaCz.fields.updatedBy)
		val raCz = new StringFieldValue(self, RaCz.fields.raCz)
	}
}

object RaCz extends StorableObject[RaCz] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "RA_CZ"

	object fields extends FieldsObject {
		val eventId = new IntDatabaseField(self, "EVENT_ID")
		val signoutId = new IntDatabaseField(self, "SIGNOUT_ID")
		val eventDatetime = new DateTimeDatabaseField(self, "EVENT_DATETIME")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val raCz = new StringDatabaseField(self, "RA_CZ", 1)
	}

	def primaryKey: IntDatabaseField = fields.eventId
}