package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class PersonsEvent extends StorableClass(PersonsEvent) {
	override object values extends ValuesObject {
		val assignId = new IntFieldValue(self, PersonsEvent.fields.assignId)
		val personId = new IntFieldValue(self, PersonsEvent.fields.personId)
		val eventId = new IntFieldValue(self, PersonsEvent.fields.eventId)
		val createdOn = new DateTimeFieldValue(self, PersonsEvent.fields.createdOn)
		val createdBy = new StringFieldValue(self, PersonsEvent.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, PersonsEvent.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, PersonsEvent.fields.updatedBy)
	}
}

object PersonsEvent extends StorableObject[PersonsEvent] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "PERSONS_EVENTS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		@NullableInDatabase
		val personId = new IntDatabaseField(self, "PERSON_ID")
		@NullableInDatabase
		val eventId = new IntDatabaseField(self, "EVENT_ID")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.assignId
}