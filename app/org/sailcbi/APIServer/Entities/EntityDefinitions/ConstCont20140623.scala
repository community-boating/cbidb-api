package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ConstCont20140623 extends StorableClass(ConstCont20140623) {
	override object values extends ValuesObject {
		val id = new IntFieldValue(self, ConstCont20140623.fields.id)
		val firstName = new NullableStringFieldValue(self, ConstCont20140623.fields.firstName)
		val lastName = new NullableStringFieldValue(self, ConstCont20140623.fields.lastName)
		val company = new NullableStringFieldValue(self, ConstCont20140623.fields.company)
		val jobTitle = new NullableStringFieldValue(self, ConstCont20140623.fields.jobTitle)
		val emailAddressHome = new NullableStringFieldValue(self, ConstCont20140623.fields.emailAddressHome)
		val emailAddressOther = new NullableStringFieldValue(self, ConstCont20140623.fields.emailAddressOther)
		val streetAddressLine1Home = new NullableStringFieldValue(self, ConstCont20140623.fields.streetAddressLine1Home)
		val cityHome = new NullableStringFieldValue(self, ConstCont20140623.fields.cityHome)
		val stateprovinceHome = new NullableStringFieldValue(self, ConstCont20140623.fields.stateprovinceHome)
		val zippostalCodeHome = new NullableStringFieldValue(self, ConstCont20140623.fields.zippostalCodeHome)
		val countryHome = new NullableStringFieldValue(self, ConstCont20140623.fields.countryHome)
		val streetAddressLine1Work = new NullableStringFieldValue(self, ConstCont20140623.fields.streetAddressLine1Work)
		val cityWork = new NullableStringFieldValue(self, ConstCont20140623.fields.cityWork)
		val stateprovinceWork = new NullableStringFieldValue(self, ConstCont20140623.fields.stateprovinceWork)
		val zippostalCodeWork = new NullableStringFieldValue(self, ConstCont20140623.fields.zippostalCodeWork)
		val countryWork = new NullableStringFieldValue(self, ConstCont20140623.fields.countryWork)
		val customField1 = new NullableStringFieldValue(self, ConstCont20140623.fields.customField1)
		val customField2 = new NullableStringFieldValue(self, ConstCont20140623.fields.customField2)
		val customField3 = new NullableStringFieldValue(self, ConstCont20140623.fields.customField3)
		val emailLists = new NullableStringFieldValue(self, ConstCont20140623.fields.emailLists)
		val sourceName = new NullableStringFieldValue(self, ConstCont20140623.fields.sourceName)
		val createdAt = new StringFieldValue(self, ConstCont20140623.fields.createdAt)
		val updatedAt = new StringFieldValue(self, ConstCont20140623.fields.updatedAt)
	}
}

object ConstCont20140623 extends StorableObject[ConstCont20140623] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "CONST_CONT_2014_06_23"

	object fields extends FieldsObject {
		val id = new IntDatabaseField(self, "ID")
		val firstName = new NullableStringDatabaseField(self, "FIRST_NAME", 100)
		val lastName = new NullableStringDatabaseField(self, "LAST_NAME", 100)
		val company = new NullableStringDatabaseField(self, "COMPANY", 100)
		val jobTitle = new NullableStringDatabaseField(self, "JOB_TITLE", 100)
		val emailAddressHome = new NullableStringDatabaseField(self, "EMAIL_ADDRESS___HOME", 500)
		val emailAddressOther = new NullableStringDatabaseField(self, "EMAIL_ADDRESS___OTHER", 500)
		val streetAddressLine1Home = new NullableStringDatabaseField(self, "STREET_ADDRESS_LINE_1___HOME", 500)
		val cityHome = new NullableStringDatabaseField(self, "CITY___HOME", 50)
		val stateprovinceHome = new NullableStringDatabaseField(self, "STATEPROVINCE___HOME", 50)
		val zippostalCodeHome = new NullableStringDatabaseField(self, "ZIPPOSTAL_CODE___HOME", 50)
		val countryHome = new NullableStringDatabaseField(self, "COUNTRY___HOME", 50)
		val streetAddressLine1Work = new NullableStringDatabaseField(self, "STREET_ADDRESS_LINE_1___WORK", 50)
		val cityWork = new NullableStringDatabaseField(self, "CITY___WORK", 50)
		val stateprovinceWork = new NullableStringDatabaseField(self, "STATEPROVINCE___WORK", 50)
		val zippostalCodeWork = new NullableStringDatabaseField(self, "ZIPPOSTAL_CODE___WORK", 50)
		val countryWork = new NullableStringDatabaseField(self, "COUNTRY___WORK", 50)
		val customField1 = new NullableStringDatabaseField(self, "CUSTOM_FIELD_1", 500)
		val customField2 = new NullableStringDatabaseField(self, "CUSTOM_FIELD_2", 50)
		val customField3 = new NullableStringDatabaseField(self, "CUSTOM_FIELD_3", 50)
		val emailLists = new NullableStringDatabaseField(self, "EMAIL_LISTS", 500)
		val sourceName = new NullableStringDatabaseField(self, "SOURCE_NAME", 100)
		val createdAt = new StringDatabaseField(self, "CREATED_AT", 30)
		val updatedAt = new StringDatabaseField(self, "UPDATED_AT", 30)
	}

	def primaryKey: IntDatabaseField = fields.id
}