package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.framework.Storable.FieldValues.{IntFieldValue, NullableDateTimeFieldValue, StringFieldValue}
import com.coleji.framework.Storable.Fields.{IntDatabaseField, NullableDateTimeDatabaseField, StringDatabaseField}
import com.coleji.framework.Storable.{FieldsObject, StorableClass, StorableObject, ValuesObject}

class Event extends StorableClass(Event) {
	object values extends ValuesObject {
		val eventId = new IntFieldValue(self, Event.fields.eventId)
		val eventName = new StringFieldValue(self, Event.fields.eventName)
		val eventDateTime = new NullableDateTimeFieldValue(self, Event.fields.eventDateTime)
	}
}

object Event extends StorableObject[Event] {
	val entityName: String = "EVENTS"

	object fields extends FieldsObject {
		val eventId = new IntDatabaseField(self, "EVENT_ID")
		val eventName = new StringDatabaseField(self, "EVENT_NAME", 200)
		val eventDateTime = new NullableDateTimeDatabaseField(self, "EVENT_DATE")
	}

	def primaryKey: IntDatabaseField = fields.eventId
}