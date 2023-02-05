package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._

class FlagChange extends StorableClass(FlagChange) {
	object values extends ValuesObject {
		val changeId = new IntFieldValue(self, FlagChange.fields.changeId)
		val flag = new StringFieldValue(self, FlagChange.fields.flag)
		val changeDatetime = new DateTimeFieldValue(self, FlagChange.fields.changeDatetime)
	}
}

object FlagChange extends StorableObject[FlagChange] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	val entityName: String = "FLAG_CHANGES"

	object fields extends FieldsObject {
		val changeId = new IntDatabaseField(self, "CHANGE_ID")
		val flag = new StringDatabaseField(self, "FLAG", 1)
		val changeDatetime = new DateTimeDatabaseField(self, "CHANGE_DATETIME")
	}

	def primaryKey: IntDatabaseField = fields.changeId
}