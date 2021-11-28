package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpTeam extends StorableClass(JpTeam) {
	object values extends ValuesObject {
		val teamId = new IntFieldValue(self, JpTeam.fields.teamId)
		val teamName = new NullableStringFieldValue(self, JpTeam.fields.teamName)
		val createdOn = new NullableLocalDateTimeFieldValue(self, JpTeam.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpTeam.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, JpTeam.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpTeam.fields.updatedBy)
		val active = new NullableBooleanFIeldValue(self, JpTeam.fields.active)
		val imageFilename = new NullableStringFieldValue(self, JpTeam.fields.imageFilename)
	}
}

object JpTeam extends StorableObject[JpTeam] {
	val entityName: String = "JP_TEAMS"

	object fields extends FieldsObject {
		val teamId = new IntDatabaseField(self, "TEAM_ID")
		val teamName = new NullableStringDatabaseField(self, "TEAM_NAME", 100)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val imageFilename = new NullableStringDatabaseField(self, "IMAGE_FILENAME", 50)
	}

	def primaryKey: IntDatabaseField = fields.teamId
}