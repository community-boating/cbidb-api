package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class JpClassSection extends StorableClass(JpClassSection) {
	override object references extends ReferencesObject {
		val jpClassInstance = new Initializable[JpClassInstance]
		val sectionLookup = new Initializable[JpClassSectionLookup]
	}

	override object values extends ValuesObject {
		val sectionId = new IntFieldValue(self, JpClassSection.fields.sectionId)
		val instanceId = new IntFieldValue(self, JpClassSection.fields.instanceId)
		val lookupId = new IntFieldValue(self, JpClassSection.fields.lookupId)
		val createdOn = new DateTimeFieldValue(self, JpClassSection.fields.createdOn)
		val createdBy = new StringFieldValue(self, JpClassSection.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, JpClassSection.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, JpClassSection.fields.updatedBy)
		val locationId = new NullableIntFieldValue(self, JpClassSection.fields.locationId)
		val instructorId = new NullableIntFieldValue(self, JpClassSection.fields.instructorId)
	}
}

object JpClassSection extends StorableObject[JpClassSection] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "JP_CLASS_SECTIONS"

	object fields extends FieldsObject {
		val sectionId = new IntDatabaseField(self, "SECTION_ID")
		@NullableInDatabase
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		@NullableInDatabase
		val lookupId = new IntDatabaseField(self, "LOOKUP_ID")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val locationId = new NullableIntDatabaseField(self, "LOCATION_ID")
		val instructorId = new NullableIntDatabaseField(self, "INSTRUCTOR_ID")
	}

	def primaryKey: IntDatabaseField = fields.sectionId
}