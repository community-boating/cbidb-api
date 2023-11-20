package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

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
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val eventId = new IntDatabaseField(self, "EVENT_ID")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.assignId
}