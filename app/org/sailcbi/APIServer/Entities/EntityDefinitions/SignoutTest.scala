package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues.{IntFieldValue, NullableStringFieldValue}
import com.coleji.neptune.Storable.Fields.{IntDatabaseField, NullableStringDatabaseField}
import com.coleji.neptune.Storable.{FieldsObject, ReferencesObject, StorableClass, StorableObject, ValuesObject}
import com.coleji.neptune.Util.Initializable

class SignoutTest extends StorableClass(SignoutTest) {
	override object references extends ReferencesObject {
		val signout = new Initializable[Signout]
		val person = new Initializable[Person]
	}

	object values extends ValuesObject {
		val testId = new IntFieldValue(self, SignoutTest.fields.testId)
		val signoutId = new IntFieldValue(self, SignoutTest.fields.signoutId)
		val personId = new IntFieldValue(self, SignoutTest.fields.personId)
		val ratingId = new IntFieldValue(self, SignoutTest.fields.ratingId)
		val testResult = new NullableStringFieldValue(self, SignoutTest.fields.testResult)
		val instructorString = new NullableStringFieldValue(self, SignoutTest.fields.instructorString)
	}
}

object SignoutTest extends StorableObject[SignoutTest] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	val entityName: String = "SIGNOUTS_TESTS"

	object fields extends FieldsObject {
		val testId = new IntDatabaseField(self, "TEST_ID")
		val signoutId = new IntDatabaseField(self, "SIGNOUT_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val ratingId = new IntDatabaseField(self, "RATING_ID")
		val testResult = new NullableStringDatabaseField(self, "TEST_RESULT", 1)
		val instructorString = new NullableStringDatabaseField(self, "INSTRUCTOR_STRING", 100)
	}

	def primaryKey: IntDatabaseField = fields.testId
}