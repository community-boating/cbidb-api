package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class Event extends StorableClass(Event) {
	override object values extends ValuesObject {
		val eventId = new IntFieldValue(self, Event.fields.eventId)
		val eventName = new StringFieldValue(self, Event.fields.eventName)
		val eventDate = new NullableDateTimeFieldValue(self, Event.fields.eventDate)
		val active = new NullableBooleanFieldValue(self, Event.fields.active)
		val createdOn = new DateTimeFieldValue(self, Event.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, Event.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, Event.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, Event.fields.updatedBy)
		val displayOrder = new NullableDoubleFieldValue(self, Event.fields.displayOrder)
	}
}

object Event extends StorableObject[Event] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "EVENTS"

	object fields extends FieldsObject {
		val eventId = new IntDatabaseField(self, "EVENT_ID")
		@NullableInDatabase
		val eventName = new StringDatabaseField(self, "EVENT_NAME", 200)
		val eventDate = new NullableDateTimeDatabaseField(self, "EVENT_DATE")
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
	}

	def primaryKey: IntDatabaseField = fields.eventId
}