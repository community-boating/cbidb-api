package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ApClassFormat extends StorableClass(ApClassFormat) {
	object values extends ValuesObject {
		val formatId = new IntFieldValue(self, ApClassFormat.fields.formatId)
		val typeId = new IntFieldValue(self, ApClassFormat.fields.typeId)
		val description = new NullableStringFieldValue(self, ApClassFormat.fields.description)
		val createdOn = new NullableLocalDateTimeFieldValue(self, ApClassFormat.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ApClassFormat.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, ApClassFormat.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ApClassFormat.fields.updatedBy)
		val signupMinDefaultOverride = new NullableDoubleFieldValue(self, ApClassFormat.fields.signupMinDefaultOverride)
		val signupMaxDefaultOverride = new NullableDoubleFieldValue(self, ApClassFormat.fields.signupMaxDefaultOverride)
		val sessionCtDefault = new NullableDoubleFieldValue(self, ApClassFormat.fields.sessionCtDefault)
		val sessionLengthDefault = new NullableDoubleFieldValue(self, ApClassFormat.fields.sessionLengthDefault)
		val priceDefaultOverride = new NullableDoubleFieldValue(self, ApClassFormat.fields.priceDefaultOverride)
	}
}

object ApClassFormat extends StorableObject[ApClassFormat] {
	val entityName: String = "AP_CLASS_FORMATS"

	object fields extends FieldsObject {
		val formatId = new IntDatabaseField(self, "FORMAT_ID")
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val description = new NullableStringDatabaseField(self, "DESCRIPTION", 1000)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val signupMinDefaultOverride = new NullableDoubleDatabaseField(self, "SIGNUP_MIN_DEFAULT_OVERRIDE")
		val signupMaxDefaultOverride = new NullableDoubleDatabaseField(self, "SIGNUP_MAX_DEFAULT_OVERRIDE")
		val sessionCtDefault = new NullableDoubleDatabaseField(self, "SESSION_CT_DEFAULT")
		val sessionLengthDefault = new NullableDoubleDatabaseField(self, "SESSION_LENGTH_DEFAULT")
		val priceDefaultOverride = new NullableDoubleDatabaseField(self, "PRICE_DEFAULT_OVERRIDE")
	}

	def primaryKey: IntDatabaseField = fields.formatId
}