package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PersonsEvent extends StorableClass(PersonsEvent) {
	object values extends ValuesObject {
		val assignId = new IntFieldValue(self, PersonsEvent.fields.assignId)
		val personId = new NullableIntFieldValue(self, PersonsEvent.fields.personId)
		val eventId = new NullableIntFieldValue(self, PersonsEvent.fields.eventId)
		val createdOn = new NullableLocalDateTimeFieldValue(self, PersonsEvent.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, PersonsEvent.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, PersonsEvent.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, PersonsEvent.fields.updatedBy)
	}
}

object PersonsEvent extends StorableObject[PersonsEvent] {
	val entityName: String = "PERSONS_EVENTS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val personId = new NullableIntDatabaseField(self, "PERSON_ID")
		val eventId = new NullableIntDatabaseField(self, "EVENT_ID")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.assignId
}