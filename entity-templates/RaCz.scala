package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class RaCz extends StorableClass(RaCz) {
	object values extends ValuesObject {
		val eventId = new IntFieldValue(self, RaCz.fields.eventId)
		val signoutId = new IntFieldValue(self, RaCz.fields.signoutId)
		val eventDatetime = new NullableLocalDateTimeFieldValue(self, RaCz.fields.eventDatetime)
		val createdOn = new NullableLocalDateTimeFieldValue(self, RaCz.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, RaCz.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, RaCz.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, RaCz.fields.updatedBy)
		val raCz = new NullableBooleanFIeldValue(self, RaCz.fields.raCz)
	}
}

object RaCz extends StorableObject[RaCz] {
	val entityName: String = "RA_CZ"

	object fields extends FieldsObject {
		val eventId = new IntDatabaseField(self, "EVENT_ID")
		val signoutId = new IntDatabaseField(self, "SIGNOUT_ID")
		val eventDatetime = new NullableLocalDateTimeDatabaseField(self, "EVENT_DATETIME")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val raCz = new NullableBooleanDatabaseField(self, "RA_CZ")
	}

	def primaryKey: IntDatabaseField = fields.eventId
}