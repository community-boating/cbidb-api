package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class JpClassType extends StorableClass(JpClassType) {
	override object values extends ValuesObject {
		val typeId = new IntFieldValue(self, JpClassType.fields.typeId)
		val typeName = new StringFieldValue(self, JpClassType.fields.typeName)
		val signupMax = new DoubleFieldValue(self, JpClassType.fields.signupMax)
		val allowMultiple = new NullableBooleanFieldValue(self, JpClassType.fields.allowMultiple)
		val ratingPrereq = new NullableDoubleFieldValue(self, JpClassType.fields.ratingPrereq)
		val createdOn = new DateTimeFieldValue(self, JpClassType.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpClassType.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, JpClassType.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, JpClassType.fields.updatedBy)
		val sessionLength = new DoubleFieldValue(self, JpClassType.fields.sessionLength)
		val sessionCt = new DoubleFieldValue(self, JpClassType.fields.sessionCt)
		val ratingOverkill = new NullableDoubleFieldValue(self, JpClassType.fields.ratingOverkill)
		val minAge = new NullableDoubleFieldValue(self, JpClassType.fields.minAge)
		val maxAge = new NullableDoubleFieldValue(self, JpClassType.fields.maxAge)
		val minSessionsForAttended = new DoubleFieldValue(self, JpClassType.fields.minSessionsForAttended)
		val active = new NullableBooleanFieldValue(self, JpClassType.fields.active)
		val displayOrder = new NullableDoubleFieldValue(self, JpClassType.fields.displayOrder)
		val noLimit = new NullableBooleanFieldValue(self, JpClassType.fields.noLimit)
	}
}

object JpClassType extends StorableObject[JpClassType] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "JP_CLASS_TYPES"

	object fields extends FieldsObject {
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		@NullableInDatabase
		val typeName = new StringDatabaseField(self, "TYPE_NAME", 100)
		@NullableInDatabase
		val signupMax = new DoubleDatabaseField(self, "SIGNUP_MAX")
		val allowMultiple = new NullableBooleanDatabaseField(self, "ALLOW_MULTIPLE")
		val ratingPrereq = new NullableDoubleDatabaseField(self, "RATING_PREREQ")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		@NullableInDatabase
		val sessionLength = new DoubleDatabaseField(self, "SESSION_LENGTH")
		@NullableInDatabase
		val sessionCt = new DoubleDatabaseField(self, "SESSION_CT")
		val ratingOverkill = new NullableDoubleDatabaseField(self, "RATING_OVERKILL")
		val minAge = new NullableDoubleDatabaseField(self, "MIN_AGE")
		val maxAge = new NullableDoubleDatabaseField(self, "MAX_AGE")
		@NullableInDatabase
		val minSessionsForAttended = new DoubleDatabaseField(self, "MIN_SESSIONS_FOR_ATTENDED")
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
		val noLimit = new NullableBooleanDatabaseField(self, "NO_LIMIT")
	}

	def primaryKey: IntDatabaseField = fields.typeId
}