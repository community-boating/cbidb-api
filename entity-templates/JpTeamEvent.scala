package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpTeamEvent extends StorableClass(JpTeamEvent) {
	object values extends ValuesObject {
		val eventId = new IntFieldValue(self, JpTeamEvent.fields.eventId)
		val memo = new NullableStringFieldValue(self, JpTeamEvent.fields.memo)
		val awardedBy = new NullableStringFieldValue(self, JpTeamEvent.fields.awardedBy)
		val createdOn = new NullableLocalDateTimeFieldValue(self, JpTeamEvent.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpTeamEvent.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, JpTeamEvent.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpTeamEvent.fields.updatedBy)
		val awardedDate = new NullableLocalDateTimeFieldValue(self, JpTeamEvent.fields.awardedDate)
	}
}

object JpTeamEvent extends StorableObject[JpTeamEvent] {
	val entityName: String = "JP_TEAM_EVENTS"

	object fields extends FieldsObject {
		val eventId = new IntDatabaseField(self, "EVENT_ID")
		val memo = new NullableStringDatabaseField(self, "MEMO", 4000)
		val awardedBy = new NullableStringDatabaseField(self, "AWARDED_BY", 100)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val awardedDate = new NullableLocalDateTimeDatabaseField(self, "AWARDED_DATE")
	}

	def primaryKey: IntDatabaseField = fields.eventId
}