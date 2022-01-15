package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class GiftCertState extends StorableClass(GiftCertState) {
	object values extends ValuesObject {
		val stateId = new IntFieldValue(self, GiftCertState.fields.stateId)
		val certId = new NullableIntFieldValue(self, GiftCertState.fields.certId)
		val stateDate = new NullableLocalDateTimeFieldValue(self, GiftCertState.fields.stateDate)
		val ownerPersonId = new NullableIntFieldValue(self, GiftCertState.fields.ownerPersonId)
		val membershipTypeId = new NullableIntFieldValue(self, GiftCertState.fields.membershipTypeId)
		val value = new NullableDoubleFieldValue(self, GiftCertState.fields.value)
		val createdOn = new NullableLocalDateTimeFieldValue(self, GiftCertState.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, GiftCertState.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, GiftCertState.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, GiftCertState.fields.updatedBy)
	}
}

object GiftCertState extends StorableObject[GiftCertState] {
	val entityName: String = "GIFT_CERT_STATES"

	object fields extends FieldsObject {
		val stateId = new IntDatabaseField(self, "STATE_ID")
		val certId = new NullableIntDatabaseField(self, "CERT_ID")
		val stateDate = new NullableLocalDateTimeDatabaseField(self, "STATE_DATE")
		val ownerPersonId = new NullableIntDatabaseField(self, "OWNER_PERSON_ID")
		val membershipTypeId = new NullableIntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
		val value = new NullableDoubleDatabaseField(self, "VALUE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.stateId
}