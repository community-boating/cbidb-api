package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._

class FoCurrentClose extends StorableClass(FoCurrentClose) {
	override object values extends ValuesObject {
		val rowId = new IntFieldValue(self, FoCurrentClose.fields.rowId)
		val inPersonId = new IntFieldValue(self, FoCurrentClose.fields.inPersonId)
		val onlineId = new IntFieldValue(self, FoCurrentClose.fields.onlineId)
	}
}

object FoCurrentClose extends StorableObject[FoCurrentClose] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "FO_CURRENT_CLOSES"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val inPersonId = new IntDatabaseField(self, "IN_PERSON_ID")
		val onlineId = new IntDatabaseField(self, "ONLINE_ID")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}