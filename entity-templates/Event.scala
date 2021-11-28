package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class Event extends StorableClass(Event) {
	object values extends ValuesObject {
		val eventId = new IntFieldValue(self, Event.fields.eventId)
		val eventName = new NullableStringFieldValue(self, Event.fields.eventName)
		val eventDate = new NullableLocalDateTimeFieldValue(self, Event.fields.eventDate)
		val active = new NullableBooleanFIeldValue(self, Event.fields.active)
		val createdOn = new NullableLocalDateTimeFieldValue(self, Event.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, Event.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, Event.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, Event.fields.updatedBy)
		val displayOrder = new NullableDoubleFieldValue(self, Event.fields.displayOrder)
	}
}

object Event extends StorableObject[Event] {
	val entityName: String = "EVENTS"

	object fields extends FieldsObject {
		val eventId = new IntDatabaseField(self, "EVENT_ID")
		val eventName = new NullableStringDatabaseField(self, "EVENT_NAME", 200)
		val eventDate = new NullableLocalDateTimeDatabaseField(self, "EVENT_DATE")
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
	}

	def primaryKey: IntDatabaseField = fields.eventId
}