package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ClassInstructor extends StorableClass(ClassInstructor) {
	override object values extends ValuesObject {
		val instructorId = new IntFieldValue(self, ClassInstructor.fields.instructorId)
		val nameFirst = new StringFieldValue(self, ClassInstructor.fields.nameFirst)
		val nameLast = new StringFieldValue(self, ClassInstructor.fields.nameLast)
		val createdOn = new NullableDateTimeFieldValue(self, ClassInstructor.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ClassInstructor.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, ClassInstructor.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ClassInstructor.fields.updatedBy)
		val active = new NullableBooleanFieldValue(self, ClassInstructor.fields.active)
	}
}

object ClassInstructor extends StorableObject[ClassInstructor] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "CLASS_INSTRUCTORS"

	object fields extends FieldsObject {
		val instructorId = new IntDatabaseField(self, "INSTRUCTOR_ID")
		val nameFirst = new StringDatabaseField(self, "NAME_FIRST", 100)
		val nameLast = new StringDatabaseField(self, "NAME_LAST", 100)
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
	}

	def primaryKey: IntDatabaseField = fields.instructorId
}