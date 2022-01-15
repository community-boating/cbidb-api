package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ClassInstructor extends StorableClass(ClassInstructor) {
	object values extends ValuesObject {
		val instructorId = new IntFieldValue(self, ClassInstructor.fields.instructorId)
		val nameFirst = new NullableStringFieldValue(self, ClassInstructor.fields.nameFirst)
		val nameLast = new NullableStringFieldValue(self, ClassInstructor.fields.nameLast)
		val createdOn = new NullableLocalDateTimeFieldValue(self, ClassInstructor.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ClassInstructor.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, ClassInstructor.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ClassInstructor.fields.updatedBy)
		val active = new NullableBooleanFIeldValue(self, ClassInstructor.fields.active)
	}
}

object ClassInstructor extends StorableObject[ClassInstructor] {
	val entityName: String = "CLASS_INSTRUCTORS"

	object fields extends FieldsObject {
		val instructorId = new IntDatabaseField(self, "INSTRUCTOR_ID")
		val nameFirst = new NullableStringDatabaseField(self, "NAME_FIRST", 100)
		val nameLast = new NullableStringDatabaseField(self, "NAME_LAST", 100)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
	}

	def primaryKey: IntDatabaseField = fields.instructorId
}