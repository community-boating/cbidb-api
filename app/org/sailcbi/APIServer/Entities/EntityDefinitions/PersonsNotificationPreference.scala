package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PersonsNotificationPreference extends StorableClass(PersonsNotificationPreference) {
	override object values extends ValuesObject {
		val prefId = new IntFieldValue(self, PersonsNotificationPreference.fields.prefId)
		val personId = new IntFieldValue(self, PersonsNotificationPreference.fields.personId)
		val notificationEvent = new StringFieldValue(self, PersonsNotificationPreference.fields.notificationEvent)
		val notificationMethod = new StringFieldValue(self, PersonsNotificationPreference.fields.notificationMethod)
		val createdOn = new NullableDateTimeFieldValue(self, PersonsNotificationPreference.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, PersonsNotificationPreference.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, PersonsNotificationPreference.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, PersonsNotificationPreference.fields.updatedBy)
	}
}

object PersonsNotificationPreference extends StorableObject[PersonsNotificationPreference] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "PERSONS_NOTIFICATION_PREFERENCES"

	object fields extends FieldsObject {
		val prefId = new IntDatabaseField(self, "PREF_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val notificationEvent = new StringDatabaseField(self, "NOTIFICATION_EVENT", 25)
		val notificationMethod = new StringDatabaseField(self, "NOTIFICATION_METHOD", 25)
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.prefId
}