package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.framework.Storable.FieldValues.IntFieldValue
import com.coleji.framework.Storable.Fields.IntDatabaseField
import com.coleji.framework.Storable._
import com.coleji.framework.Util.Initializable
import org.sailcbi.APIServer.Entities.NullableInDatabase

class JpClassSection extends StorableClass(JpClassSection) {
	override object references extends ReferencesObject {
		val jpClassInstance = new Initializable[JpClassInstance]
		val sectionLookup = new Initializable[JpClassSectionLookup]
	}

	object values extends ValuesObject {
		val sectionId = new IntFieldValue(self, JpClassSection.fields.sectionId)
		val instanceId = new IntFieldValue(self, JpClassSection.fields.instanceId)
		val lookupId = new IntFieldValue(self, JpClassSection.fields.lookupId)
	}
}

object JpClassSection extends StorableObject[JpClassSection] {
	val entityName: String = "JP_CLASS_SECTIONS"

	object fields extends FieldsObject {
		val sectionId = new IntDatabaseField(self, "SECTION_ID")
		@NullableInDatabase
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		@NullableInDatabase
		val lookupId = new IntDatabaseField(self, "LOOKUP_ID")
	}

	def primaryKey: IntDatabaseField = fields.sectionId
}