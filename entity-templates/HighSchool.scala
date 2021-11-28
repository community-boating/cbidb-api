package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class HighSchool extends StorableClass(HighSchool) {
	object values extends ValuesObject {
		val schoolId = new IntFieldValue(self, HighSchool.fields.schoolId)
		val schoolName = new NullableStringFieldValue(self, HighSchool.fields.schoolName)
		val createdOn = new NullableLocalDateTimeFieldValue(self, HighSchool.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, HighSchool.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, HighSchool.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, HighSchool.fields.updatedBy)
		val active = new NullableBooleanFIeldValue(self, HighSchool.fields.active)
		val displayOrder = new NullableDoubleFieldValue(self, HighSchool.fields.displayOrder)
	}
}

object HighSchool extends StorableObject[HighSchool] {
	val entityName: String = "HIGH_SCHOOLS"

	object fields extends FieldsObject {
		val schoolId = new IntDatabaseField(self, "SCHOOL_ID")
		val schoolName = new NullableStringDatabaseField(self, "SCHOOL_NAME", 200)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
	}

	def primaryKey: IntDatabaseField = fields.schoolId
}