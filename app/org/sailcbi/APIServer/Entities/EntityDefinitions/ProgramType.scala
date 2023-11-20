package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ProgramType extends StorableClass(ProgramType) {
	override object values extends ValuesObject {
		val programId = new IntFieldValue(self, ProgramType.fields.programId)
		val programName = new StringFieldValue(self, ProgramType.fields.programName)
		val displayOrder = new DoubleFieldValue(self, ProgramType.fields.displayOrder)
		val active = new NullableBooleanFieldValue(self, ProgramType.fields.active)
		val createdOn = new DateTimeFieldValue(self, ProgramType.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ProgramType.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, ProgramType.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, ProgramType.fields.updatedBy)
		val colorDisplay = new StringFieldValue(self, ProgramType.fields.colorDisplay)
	}
}

object ProgramType extends StorableObject[ProgramType] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "PROGRAM_TYPES"

	object fields extends FieldsObject {
		val programId = new IntDatabaseField(self, "PROGRAM_ID")
		val programName = new StringDatabaseField(self, "PROGRAM_NAME", 100)
		val displayOrder = new DoubleDatabaseField(self, "DISPLAY_ORDER")
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val colorDisplay = new StringDatabaseField(self, "COLOR_DISPLAY", 4000)
	}

	def primaryKey: IntDatabaseField = fields.programId
}