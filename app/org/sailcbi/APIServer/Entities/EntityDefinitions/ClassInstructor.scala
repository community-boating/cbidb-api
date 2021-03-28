package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.framework.Storable.FieldValues.{IntFieldValue, StringFieldValue}
import com.coleji.framework.Storable.Fields.{IntDatabaseField, StringDatabaseField}
import com.coleji.framework.Storable.{FieldsObject, ReferencesObject, StorableClass, StorableObject, ValuesObject}
import com.coleji.framework.Storable._

class ClassInstructor extends StorableClass {
	this.setCompanion(ClassInstructor)

	object references extends ReferencesObject {}

	object values extends ValuesObject {
		val instructorId = new IntFieldValue(self, ClassInstructor.fields.instructorId)
		val nameFirst = new StringFieldValue(self, ClassInstructor.fields.nameFirst)
		val nameLast = new StringFieldValue(self, ClassInstructor.fields.nameLast)
	}

	override val valuesList = List(
		values.instructorId,
		values.nameFirst,
		values.nameLast
	)
}

object ClassInstructor extends StorableObject[ClassInstructor] {
	val entityName: String = "CLASS_INSTRUCTORS"

	object fields extends FieldsObject {
		val instructorId = new IntDatabaseField(self, "INSTRUCTOR_ID")
		val nameFirst = new StringDatabaseField(self, "NAME_FIRST", 100)
		val nameLast = new StringDatabaseField(self, "NAME_LAST", 100)
	}

	def primaryKey: IntDatabaseField = fields.instructorId
}