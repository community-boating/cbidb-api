package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues.{BooleanFieldValue, IntFieldValue, StringFieldValue}
import com.coleji.neptune.Storable.Fields.{BooleanDatabaseField, IntDatabaseField, StringDatabaseField}
import com.coleji.neptune.Storable.{FieldsObject, StorableClass, StorableObject, ValuesObject}

class HighSchool extends StorableClass(HighSchool) {
	object values extends ValuesObject {
		val schoolId = new IntFieldValue(self, HighSchool.fields.schoolId)
		val schoolName = new StringFieldValue(self, HighSchool.fields.schoolName)
		val active = new BooleanFieldValue(self, HighSchool.fields.active)
	}
}

object HighSchool extends StorableObject[HighSchool] {
	val entityName: String = "HIGH_SCHOOLS"

	object fields extends FieldsObject {
		val schoolId = new IntDatabaseField(self, "SCHOOL_ID")
		val schoolName = new StringDatabaseField(self, "SCHOOL_NAME", 200)
		val active = new BooleanDatabaseField(self, "ACTIVE", true)
	}

	def primaryKey: IntDatabaseField = fields.schoolId
}
