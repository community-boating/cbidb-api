package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues.{IntFieldValue, StringFieldValue}
import com.coleji.neptune.Storable.Fields.{IntDatabaseField, StringDatabaseField}
import com.coleji.neptune.Storable._

class ProgramType extends StorableClass(ProgramType) {
	object values extends ValuesObject {
		val programId = new IntFieldValue(self, ProgramType.fields.programId)
		val programName = new StringFieldValue(self, ProgramType.fields.programName)
	}
}

object ProgramType extends StorableObject[ProgramType] {
	val entityName: String = "PROGRAM_TYPES"

	override val useRuntimeFieldnamesForJson: Boolean = true

	object fields extends FieldsObject {
		val programId = new IntDatabaseField(self, "PROGRAM_ID")
		val programName = new StringDatabaseField(self, "PROGRAM_NAME", 100)
	}

	def primaryKey: IntDatabaseField = fields.programId

	object specialIDs {
		val PROGRAM_TYPE_ID_AP: Int = 1
		val PROGRAM_TYPE_ID_JP: Int = 2
		val PROGRAM_TYPE_ID_UAP: Int = 3
		val PROGRAM_TYPE_ID_HS: Int = 4
	}

}