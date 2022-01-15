package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpSpringWaitlist extends StorableClass(JpSpringWaitlist) {
	object values extends ValuesObject {
		val regId = new IntFieldValue(self, JpSpringWaitlist.fields.regId)
		val membershipTypeId = new NullableIntFieldValue(self, JpSpringWaitlist.fields.membershipTypeId)
		val personId = new NullableIntFieldValue(self, JpSpringWaitlist.fields.personId)
		val createdOn = new NullableLocalDateTimeFieldValue(self, JpSpringWaitlist.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpSpringWaitlist.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, JpSpringWaitlist.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpSpringWaitlist.fields.updatedBy)
		val waitBegin = new NullableLocalDateTimeFieldValue(self, JpSpringWaitlist.fields.waitBegin)
	}
}

object JpSpringWaitlist extends StorableObject[JpSpringWaitlist] {
	val entityName: String = "JP_SPRING_WAITLIST"

	object fields extends FieldsObject {
		val regId = new IntDatabaseField(self, "REG_ID")
		val membershipTypeId = new NullableIntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
		val personId = new NullableIntDatabaseField(self, "PERSON_ID")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val waitBegin = new NullableLocalDateTimeDatabaseField(self, "WAIT_BEGIN")
	}

	def primaryKey: IntDatabaseField = fields.regId
}