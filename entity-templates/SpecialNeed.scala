package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class SpecialNeed extends StorableClass(SpecialNeed) {
	object values extends ValuesObject {
		val specId = new IntFieldValue(self, SpecialNeed.fields.specId)
		val personId = new NullableIntFieldValue(self, SpecialNeed.fields.personId)
		val membershipTypeId = new NullableIntFieldValue(self, SpecialNeed.fields.membershipTypeId)
		val status = new NullableBooleanFIeldValue(self, SpecialNeed.fields.status)
		val overridePrice = new NullableDoubleFieldValue(self, SpecialNeed.fields.overridePrice)
		val authOn = new NullableLocalDateTimeFieldValue(self, SpecialNeed.fields.authOn)
		val authBy = new NullableStringFieldValue(self, SpecialNeed.fields.authBy)
		val createdOn = new NullableLocalDateTimeFieldValue(self, SpecialNeed.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, SpecialNeed.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, SpecialNeed.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, SpecialNeed.fields.updatedBy)
		val description = new NullableUnknownFieldType(self, SpecialNeed.fields.description)
		val contactMethod = new NullableStringFieldValue(self, SpecialNeed.fields.contactMethod)
		val phone = new NullableStringFieldValue(self, SpecialNeed.fields.phone)
	}
}

object SpecialNeed extends StorableObject[SpecialNeed] {
	val entityName: String = "SPECIAL_NEEDS"

	object fields extends FieldsObject {
		val specId = new IntDatabaseField(self, "SPEC_ID")
		val personId = new NullableIntDatabaseField(self, "PERSON_ID")
		val membershipTypeId = new NullableIntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
		val status = new NullableBooleanDatabaseField(self, "STATUS")
		val overridePrice = new NullableDoubleDatabaseField(self, "OVERRIDE_PRICE")
		val authOn = new NullableLocalDateTimeDatabaseField(self, "AUTH_ON")
		val authBy = new NullableStringDatabaseField(self, "AUTH_BY", 50)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val description = new NullableUnknownFieldType(self, "DESCRIPTION")
		val contactMethod = new NullableStringDatabaseField(self, "CONTACT_METHOD", 50)
		val phone = new NullableStringDatabaseField(self, "PHONE", 20)
	}

	def primaryKey: IntDatabaseField = fields.specId
}