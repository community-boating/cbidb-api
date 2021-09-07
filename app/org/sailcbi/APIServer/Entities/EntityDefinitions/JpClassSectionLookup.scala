package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.framework.Storable.FieldValues.{IntFieldValue, StringFieldValue}
import com.coleji.framework.Storable.Fields.{IntDatabaseField, StringDatabaseField}
import com.coleji.framework.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class JpClassSectionLookup extends StorableClass(JpClassSectionLookup) {
	override object references extends ReferencesObject {
	}

	object values extends ValuesObject {
		val sectionId = new IntFieldValue(self, JpClassSectionLookup.fields.sectionId)
		val sectionName = new StringFieldValue(self, JpClassSectionLookup.fields.sectionName)
		val svgUrl = new StringFieldValue(self, JpClassSectionLookup.fields.svgUrl)
	}

}

object JpClassSectionLookup extends StorableObject[JpClassSectionLookup] {
	val entityName: String = "JP_CLASS_SECTION_LOOKUP"

	object fields extends FieldsObject {
		val sectionId = new IntDatabaseField(self, "SECTION_ID")
		@NullableInDatabase
		val sectionName = new StringDatabaseField(self, "SECTION_NAME", 50)
		@NullableInDatabase
		val svgUrl = new StringDatabaseField(self, "SVG_URL", 100)
	}

	def primaryKey: IntDatabaseField = fields.sectionId
}
