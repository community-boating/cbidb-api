package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class ApClassFormat extends StorableClass(ApClassFormat) {
	override object references extends ReferencesObject {
		val apClassType = new Initializable[ApClassType]
	}

	override object values extends ValuesObject {
		val formatId = new IntFieldValue(self, ApClassFormat.fields.formatId)
		val typeId = new IntFieldValue(self, ApClassFormat.fields.typeId)
		val description = new NullableStringFieldValue(self, ApClassFormat.fields.description)
		val createdOn = new DateTimeFieldValue(self, ApClassFormat.fields.createdOn)
		val createdBy = new StringFieldValue(self, ApClassFormat.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, ApClassFormat.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, ApClassFormat.fields.updatedBy)
		val priceDefaultOverride = new NullableDoubleFieldValue(self, ApClassFormat.fields.priceDefaultOverride)
		val sessionCtDefault = new IntFieldValue(self, ApClassFormat.fields.sessionCtDefault)
		val sessionLengthDefault = new DoubleFieldValue(self, ApClassFormat.fields.sessionLengthDefault)
		val signupMaxDefaultOverride = new NullableIntFieldValue(self, ApClassFormat.fields.signupMaxDefaultOverride)
		val signupMinDefaultOverride = new NullableIntFieldValue(self, ApClassFormat.fields.signupMinDefaultOverride)
	}
}

object ApClassFormat extends StorableObject[ApClassFormat] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "AP_CLASS_FORMATS"

	object fields extends FieldsObject {
		val formatId = new IntDatabaseField(self, "FORMAT_ID")
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val description = new NullableStringDatabaseField(self, "DESCRIPTION", 1000)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val priceDefaultOverride = new NullableDoubleDatabaseField(self, "PRICE_DEFAULT_OVERRIDE")
		val sessionCtDefault = new IntDatabaseField(self, "SESSION_CT_DEFAULT")
		val sessionLengthDefault = new DoubleDatabaseField(self, "SESSION_LENGTH_DEFAULT")
		val signupMaxDefaultOverride = new NullableIntDatabaseField(self, "SIGNUP_MAX_DEFAULT_OVERRIDE")
		val signupMinDefaultOverride = new NullableIntDatabaseField(self, "SIGNUP_MIN_DEFAULT_OVERRIDE")
	}

	def primaryKey: IntDatabaseField = fields.formatId
}