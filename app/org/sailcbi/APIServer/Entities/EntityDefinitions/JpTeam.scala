package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues.{IntFieldValue, StringFieldValue}
import com.coleji.neptune.Storable.Fields.{IntDatabaseField, StringDatabaseField}
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class JpTeam extends StorableClass(JpTeam) {
	object values extends ValuesObject {
		val teamId = new IntFieldValue(self, JpTeam.fields.teamId)
		val teamName = new StringFieldValue(self, JpTeam.fields.teamName)
	}
}

object JpTeam extends StorableObject[JpTeam] {
	val entityName: String = "JP_TEAMS"

	object fields extends FieldsObject {
		val teamId = new IntDatabaseField(self, "TEAM_ID")
		@NullableInDatabase
		val teamName = new StringDatabaseField(self, "TEAM_NAME", 100)
	}

	def primaryKey: IntDatabaseField = fields.teamId
}