package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.framework.Storable.FieldValues.{IntFieldValue, StringFieldValue}
import com.coleji.framework.Storable.Fields.{IntDatabaseField, StringDatabaseField}
import com.coleji.framework.Storable._

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
		val teamName = new StringDatabaseField(self, "TEAM_NAME", 100)
	}

	def primaryKey: IntDatabaseField = fields.teamId
}