package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ProgramType extends StorableClass(ProgramType) {
	object values extends ValuesObject {
		val programId = new IntFieldValue(self, ProgramType.fields.programId)
		val programName = new StringFieldValue(self, ProgramType.fields.programName)
		val displayOrder = new NullableDoubleFieldValue(self, ProgramType.fields.displayOrder)
		val active = new NullableBooleanFIeldValue(self, ProgramType.fields.active)
		val createdOn = new NullableLocalDateTimeFieldValue(self, ProgramType.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ProgramType.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, ProgramType.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ProgramType.fields.updatedBy)
		val colorDisplay = new NullableStringFieldValue(self, ProgramType.fields.colorDisplay)
	}
}

object ProgramType extends StorableObject[ProgramType] {
	val entityName: String = "PROGRAM_TYPES"

	object fields extends FieldsObject {
		val programId = new IntDatabaseField(self, "PROGRAM_ID")
		val programName = new StringDatabaseField(self, "PROGRAM_NAME", 100)
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val colorDisplay = new NullableStringDatabaseField(self, "COLOR_DISPLAY", 4000)
	}

	def primaryKey: IntDatabaseField = fields.programId
}