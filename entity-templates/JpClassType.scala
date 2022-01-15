package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpClassType extends StorableClass(JpClassType) {
	object values extends ValuesObject {
		val typeId = new IntFieldValue(self, JpClassType.fields.typeId)
		val typeName = new NullableStringFieldValue(self, JpClassType.fields.typeName)
		val signupMax = new NullableDoubleFieldValue(self, JpClassType.fields.signupMax)
		val allowMultiple = new NullableBooleanFIeldValue(self, JpClassType.fields.allowMultiple)
		val ratingPrereq = new NullableDoubleFieldValue(self, JpClassType.fields.ratingPrereq)
		val createdOn = new NullableLocalDateTimeFieldValue(self, JpClassType.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpClassType.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, JpClassType.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpClassType.fields.updatedBy)
		val sessionLength = new NullableDoubleFieldValue(self, JpClassType.fields.sessionLength)
		val sessionCt = new NullableDoubleFieldValue(self, JpClassType.fields.sessionCt)
		val ratingOverkill = new NullableDoubleFieldValue(self, JpClassType.fields.ratingOverkill)
		val minAge = new NullableDoubleFieldValue(self, JpClassType.fields.minAge)
		val maxAge = new NullableDoubleFieldValue(self, JpClassType.fields.maxAge)
		val minSessionsForAttended = new NullableDoubleFieldValue(self, JpClassType.fields.minSessionsForAttended)
		val active = new NullableBooleanFIeldValue(self, JpClassType.fields.active)
		val displayOrder = new NullableDoubleFieldValue(self, JpClassType.fields.displayOrder)
		val noLimit = new NullableBooleanFIeldValue(self, JpClassType.fields.noLimit)
	}
}

object JpClassType extends StorableObject[JpClassType] {
	val entityName: String = "JP_CLASS_TYPES"

	object fields extends FieldsObject {
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val typeName = new NullableStringDatabaseField(self, "TYPE_NAME", 100)
		val signupMax = new NullableDoubleDatabaseField(self, "SIGNUP_MAX")
		val allowMultiple = new NullableBooleanDatabaseField(self, "ALLOW_MULTIPLE")
		val ratingPrereq = new NullableDoubleDatabaseField(self, "RATING_PREREQ")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val sessionLength = new NullableDoubleDatabaseField(self, "SESSION_LENGTH")
		val sessionCt = new NullableDoubleDatabaseField(self, "SESSION_CT")
		val ratingOverkill = new NullableDoubleDatabaseField(self, "RATING_OVERKILL")
		val minAge = new NullableDoubleDatabaseField(self, "MIN_AGE")
		val maxAge = new NullableDoubleDatabaseField(self, "MAX_AGE")
		val minSessionsForAttended = new NullableDoubleDatabaseField(self, "MIN_SESSIONS_FOR_ATTENDED")
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
		val noLimit = new NullableBooleanDatabaseField(self, "NO_LIMIT")
	}

	def primaryKey: IntDatabaseField = fields.typeId
}