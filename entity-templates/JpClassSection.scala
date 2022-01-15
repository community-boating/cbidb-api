package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpClassSection extends StorableClass(JpClassSection) {
	object values extends ValuesObject {
		val sectionId = new IntFieldValue(self, JpClassSection.fields.sectionId)
		val instanceId = new NullableIntFieldValue(self, JpClassSection.fields.instanceId)
		val lookupId = new NullableIntFieldValue(self, JpClassSection.fields.lookupId)
		val createdOn = new NullableLocalDateTimeFieldValue(self, JpClassSection.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpClassSection.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, JpClassSection.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpClassSection.fields.updatedBy)
		val locationId = new NullableIntFieldValue(self, JpClassSection.fields.locationId)
		val instructorId = new NullableIntFieldValue(self, JpClassSection.fields.instructorId)
	}
}

object JpClassSection extends StorableObject[JpClassSection] {
	val entityName: String = "JP_CLASS_SECTIONS"

	object fields extends FieldsObject {
		val sectionId = new IntDatabaseField(self, "SECTION_ID")
		val instanceId = new NullableIntDatabaseField(self, "INSTANCE_ID")
		val lookupId = new NullableIntDatabaseField(self, "LOOKUP_ID")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val locationId = new NullableIntDatabaseField(self, "LOCATION_ID")
		val instructorId = new NullableIntDatabaseField(self, "INSTRUCTOR_ID")
	}

	def primaryKey: IntDatabaseField = fields.sectionId
}