package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class JpTeam extends StorableClass(JpTeam) {
	override object values extends ValuesObject {
		val teamId = new IntFieldValue(self, JpTeam.fields.teamId)
		val teamName = new StringFieldValue(self, JpTeam.fields.teamName)
		val createdOn = new NullableDateTimeFieldValue(self, JpTeam.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpTeam.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, JpTeam.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpTeam.fields.updatedBy)
		val active = new NullableBooleanFieldValue(self, JpTeam.fields.active)
		val imageFilename = new StringFieldValue(self, JpTeam.fields.imageFilename)
	}
}

object JpTeam extends StorableObject[JpTeam] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "JP_TEAMS"

	object fields extends FieldsObject {
		val teamId = new IntDatabaseField(self, "TEAM_ID")
		@NullableInDatabase
		val teamName = new StringDatabaseField(self, "TEAM_NAME", 100)
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		@NullableInDatabase
		val imageFilename = new StringDatabaseField(self, "IMAGE_FILENAME", 50)
	}

	def primaryKey: IntDatabaseField = fields.teamId
}