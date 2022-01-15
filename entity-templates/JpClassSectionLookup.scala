package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpClassSectionLookup extends StorableClass(JpClassSectionLookup) {
	object values extends ValuesObject {
		val sectionId = new IntFieldValue(self, JpClassSectionLookup.fields.sectionId)
		val sectionName = new NullableStringFieldValue(self, JpClassSectionLookup.fields.sectionName)
		val createdOn = new NullableLocalDateTimeFieldValue(self, JpClassSectionLookup.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpClassSectionLookup.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, JpClassSectionLookup.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpClassSectionLookup.fields.updatedBy)
		val active = new NullableBooleanFIeldValue(self, JpClassSectionLookup.fields.active)
		val displayOrder = new NullableDoubleFieldValue(self, JpClassSectionLookup.fields.displayOrder)
		val svgUrl = new NullableStringFieldValue(self, JpClassSectionLookup.fields.svgUrl)
	}
}

object JpClassSectionLookup extends StorableObject[JpClassSectionLookup] {
	val entityName: String = "JP_CLASS_SECTION_LOOKUP"

	object fields extends FieldsObject {
		val sectionId = new IntDatabaseField(self, "SECTION_ID")
		val sectionName = new NullableStringDatabaseField(self, "SECTION_NAME", 50)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
		val svgUrl = new NullableStringDatabaseField(self, "SVG_URL", 100)
	}

	def primaryKey: IntDatabaseField = fields.sectionId
}