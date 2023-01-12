package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ApClassFormat extends StorableClass(ApClassFormat) {
	override object references extends ReferencesObject {
		val apClassType = new Initializable[ApClassType]
	}

	object values extends ValuesObject {
		val formatId = new IntFieldValue(self, ApClassFormat.fields.formatId)
		val typeId = new IntFieldValue(self, ApClassFormat.fields.typeId)
		val description = new NullableStringFieldValue(self, ApClassFormat.fields.description)
		val signupMinDefaultOverride = new NullableIntFieldValue(self, ApClassFormat.fields.signupMinDefaultOverride)
		val signupMaxDefaultOverride = new NullableIntFieldValue(self, ApClassFormat.fields.signupMaxDefaultOverride)
		val sessionCtDefault = new IntFieldValue(self, ApClassFormat.fields.sessionCtDefault)
		val sessionLengthDefault = new DoubleFieldValue(self, ApClassFormat.fields.sessionLengthDefault)
		val priceDefaultOverride = new NullableDoubleFieldValue(self, ApClassFormat.fields.priceDefaultOverride)
	}
}

object ApClassFormat extends StorableObject[ApClassFormat] {
	val entityName: String = "AP_CLASS_FORMATS"

	override val useRuntimeFieldnamesForJson: Boolean = true

	object fields extends FieldsObject {
		val formatId = new IntDatabaseField(self, "FORMAT_ID")
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val description = new NullableStringDatabaseField(self, "DESCRIPTION", 1000)
		val signupMinDefaultOverride = new NullableIntDatabaseField(self, "SIGNUP_MIN_DEFAULT_OVERRIDE")
		val signupMaxDefaultOverride = new NullableIntDatabaseField(self, "SIGNUP_MAX_DEFAULT_OVERRIDE")
		val sessionCtDefault = new IntDatabaseField(self, "SESSION_CT_DEFAULT")
		val sessionLengthDefault = new DoubleDatabaseField(self, "SESSION_LENGTH_DEFAULT")
		val priceDefaultOverride = new NullableDoubleDatabaseField(self, "PRICE_DEFAULT_OVERRIDE")
	}

	def primaryKey: IntDatabaseField = fields.formatId
}
