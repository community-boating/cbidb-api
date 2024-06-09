package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class FoArSource extends StorableClass(FoArSource) {
	override object values extends ValuesObject {
		val sourceId = new IntFieldValue(self, FoArSource.fields.sourceId)
		val sourceName = new StringFieldValue(self, FoArSource.fields.sourceName)
		val active = new NullableBooleanFieldValue(self, FoArSource.fields.active)
		val displayOrder = new DoubleFieldValue(self, FoArSource.fields.displayOrder)
		val createdOn = new NullableDateTimeFieldValue(self, FoArSource.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, FoArSource.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, FoArSource.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, FoArSource.fields.updatedBy)
		val isOnline = new BooleanFieldValue(self, FoArSource.fields.isOnline)
		val readOnly = new NullableBooleanFieldValue(self, FoArSource.fields.readOnly)
	}
}

object FoArSource extends StorableObject[FoArSource] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "FO_AR_SOURCES"

	object fields extends FieldsObject {
		val sourceId = new IntDatabaseField(self, "SOURCE_ID")
		val sourceName = new StringDatabaseField(self, "SOURCE_NAME", 100)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		@NullableInDatabase
		val displayOrder = new DoubleDatabaseField(self, "DISPLAY_ORDER")
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val isOnline = new BooleanDatabaseField(self, "IS_ONLINE", false)
		val readOnly = new NullableBooleanDatabaseField(self, "READ_ONLY")
	}

	def primaryKey: IntDatabaseField = fields.sourceId
}