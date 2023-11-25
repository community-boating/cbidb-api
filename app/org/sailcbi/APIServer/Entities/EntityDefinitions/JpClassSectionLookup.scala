package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class JpClassSectionLookup extends StorableClass(JpClassSectionLookup) {
	override object values extends ValuesObject {
		val sectionId = new IntFieldValue(self, JpClassSectionLookup.fields.sectionId)
		val sectionName = new StringFieldValue(self, JpClassSectionLookup.fields.sectionName)
		val createdOn = new DateTimeFieldValue(self, JpClassSectionLookup.fields.createdOn)
		val createdBy = new StringFieldValue(self, JpClassSectionLookup.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, JpClassSectionLookup.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, JpClassSectionLookup.fields.updatedBy)
		val active = new NullableBooleanFieldValue(self, JpClassSectionLookup.fields.active)
		val displayOrder = new DoubleFieldValue(self, JpClassSectionLookup.fields.displayOrder)
		val svgUrl = new StringFieldValue(self, JpClassSectionLookup.fields.svgUrl)
	}
}

object JpClassSectionLookup extends StorableObject[JpClassSectionLookup] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "JP_CLASS_SECTION_LOOKUP"

	object fields extends FieldsObject {
		val sectionId = new IntDatabaseField(self, "SECTION_ID")
		@NullableInDatabase
		val sectionName = new StringDatabaseField(self, "SECTION_NAME", 50)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		@NullableInDatabase
		val displayOrder = new DoubleDatabaseField(self, "DISPLAY_ORDER")
		@NullableInDatabase
		val svgUrl = new StringDatabaseField(self, "SVG_URL", 100)
	}

	def primaryKey: IntDatabaseField = fields.sectionId
}