package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ApClassType extends StorableClass(ApClassType) {
	object values extends ValuesObject {
		val typeId = new IntFieldValue(self, ApClassType.fields.typeId)
		val typeName = new StringFieldValue(self, ApClassType.fields.typeName)
		val ratingPrereq = new NullableDoubleFieldValue(self, ApClassType.fields.ratingPrereq)
		val classPrereq = new NullableDoubleFieldValue(self, ApClassType.fields.classPrereq)
		val ratingOverkill = new NullableDoubleFieldValue(self, ApClassType.fields.ratingOverkill)
		val createdOn = new NullableLocalDateTimeFieldValue(self, ApClassType.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ApClassType.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, ApClassType.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ApClassType.fields.updatedBy)
		val displayOrder = new NullableDoubleFieldValue(self, ApClassType.fields.displayOrder)
		val descShort = new NullableUnknownFieldType(self, ApClassType.fields.descShort)
		val descLong = new NullableUnknownFieldType(self, ApClassType.fields.descLong)
		val classOverkill = new NullableDoubleFieldValue(self, ApClassType.fields.classOverkill)
		val colorCode = new NullableStringFieldValue(self, ApClassType.fields.colorCode)
		val noSignup = new NullableBooleanFIeldValue(self, ApClassType.fields.noSignup)
		val priceDefault = new NullableDoubleFieldValue(self, ApClassType.fields.priceDefault)
		val signupMinDefault = new NullableDoubleFieldValue(self, ApClassType.fields.signupMinDefault)
		val signupMaxDefault = new NullableDoubleFieldValue(self, ApClassType.fields.signupMaxDefault)
		val disallowIfOverkill = new NullableBooleanFIeldValue(self, ApClassType.fields.disallowIfOverkill)
	}
}

object ApClassType extends StorableObject[ApClassType] {
	val entityName: String = "AP_CLASS_TYPES"

	object fields extends FieldsObject {
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val typeName = new StringDatabaseField(self, "TYPE_NAME", 200)
		val ratingPrereq = new NullableDoubleDatabaseField(self, "RATING_PREREQ")
		val classPrereq = new NullableDoubleDatabaseField(self, "CLASS_PREREQ")
		val ratingOverkill = new NullableDoubleDatabaseField(self, "RATING_OVERKILL")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
		val descShort = new NullableUnknownFieldType(self, "DESC_SHORT")
		val descLong = new NullableUnknownFieldType(self, "DESC_LONG")
		val classOverkill = new NullableDoubleDatabaseField(self, "CLASS_OVERKILL")
		val colorCode = new NullableStringDatabaseField(self, "COLOR_CODE")
		val noSignup = new NullableBooleanDatabaseField(self, "NO_SIGNUP")
		val priceDefault = new NullableDoubleDatabaseField(self, "PRICE_DEFAULT")
		val signupMinDefault = new NullableDoubleDatabaseField(self, "SIGNUP_MIN_DEFAULT")
		val signupMaxDefault = new NullableDoubleDatabaseField(self, "SIGNUP_MAX_DEFAULT")
		val disallowIfOverkill = new NullableBooleanDatabaseField(self, "DISALLOW_IF_OVERKILL")
	}

	def primaryKey: IntDatabaseField = fields.typeId
}