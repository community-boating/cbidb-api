package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class SignoutTest extends StorableClass(SignoutTest) {
	override object references extends ReferencesObject {
		val signout = new Initializable[Signout]
		val person = new Initializable[Person]
	}

	override object values extends ValuesObject {
		val testId = new IntFieldValue(self, SignoutTest.fields.testId)
		val signoutId = new IntFieldValue(self, SignoutTest.fields.signoutId)
		val personId = new IntFieldValue(self, SignoutTest.fields.personId)
		val ratingId = new IntFieldValue(self, SignoutTest.fields.ratingId)
		val testResult = new NullableStringFieldValue(self, SignoutTest.fields.testResult)
		val instructorString = new NullableStringFieldValue(self, SignoutTest.fields.instructorString)
		val createdOn = new DateTimeFieldValue(self, SignoutTest.fields.createdOn)
		val createdBy = new StringFieldValue(self, SignoutTest.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, SignoutTest.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, SignoutTest.fields.updatedBy)
	}
}

object SignoutTest extends StorableObject[SignoutTest] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "SIGNOUTS_TESTS"

	object fields extends FieldsObject {
		val testId = new IntDatabaseField(self, "TEST_ID")
		val signoutId = new IntDatabaseField(self, "SIGNOUT_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val ratingId = new IntDatabaseField(self, "RATING_ID")
		val testResult = new NullableStringDatabaseField(self, "TEST_RESULT", 1)
		val instructorString = new NullableStringDatabaseField(self, "INSTRUCTOR_STRING", 100)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 100)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 100)
	}

	def primaryKey: IntDatabaseField = fields.testId
}