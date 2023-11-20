package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpSpringWaitlist extends StorableClass(JpSpringWaitlist) {
	override object values extends ValuesObject {
		val regId = new IntFieldValue(self, JpSpringWaitlist.fields.regId)
		val membershipTypeId = new NullableIntFieldValue(self, JpSpringWaitlist.fields.membershipTypeId)
		val personId = new NullableIntFieldValue(self, JpSpringWaitlist.fields.personId)
		val createdOn = new DateTimeFieldValue(self, JpSpringWaitlist.fields.createdOn)
		val createdBy = new StringFieldValue(self, JpSpringWaitlist.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, JpSpringWaitlist.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, JpSpringWaitlist.fields.updatedBy)
		val waitBegin = new NullableDateTimeFieldValue(self, JpSpringWaitlist.fields.waitBegin)
	}
}

object JpSpringWaitlist extends StorableObject[JpSpringWaitlist] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "JP_SPRING_WAITLIST"

	object fields extends FieldsObject {
		val regId = new IntDatabaseField(self, "REG_ID")
		val membershipTypeId = new NullableIntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
		val personId = new NullableIntDatabaseField(self, "PERSON_ID")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val waitBegin = new NullableDateTimeDatabaseField(self, "WAIT_BEGIN")
	}

	def primaryKey: IntDatabaseField = fields.regId
}