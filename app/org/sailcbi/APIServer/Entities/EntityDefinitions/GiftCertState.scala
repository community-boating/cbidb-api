package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class GiftCertState extends StorableClass(GiftCertState) {
	override object values extends ValuesObject {
		val stateId = new IntFieldValue(self, GiftCertState.fields.stateId)
		val certId = new IntFieldValue(self, GiftCertState.fields.certId)
		val stateDate = new DateTimeFieldValue(self, GiftCertState.fields.stateDate)
		val ownerPersonId = new NullableIntFieldValue(self, GiftCertState.fields.ownerPersonId)
		val membershipTypeId = new NullableIntFieldValue(self, GiftCertState.fields.membershipTypeId)
		val value = new NullableDoubleFieldValue(self, GiftCertState.fields.value)
		val createdOn = new DateTimeFieldValue(self, GiftCertState.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, GiftCertState.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, GiftCertState.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, GiftCertState.fields.updatedBy)
	}
}

object GiftCertState extends StorableObject[GiftCertState] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "GIFT_CERT_STATES"

	object fields extends FieldsObject {
		val stateId = new IntDatabaseField(self, "STATE_ID")
		val certId = new IntDatabaseField(self, "CERT_ID")
		val stateDate = new DateTimeDatabaseField(self, "STATE_DATE")
		val ownerPersonId = new NullableIntDatabaseField(self, "OWNER_PERSON_ID")
		val membershipTypeId = new NullableIntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
		val value = new NullableDoubleDatabaseField(self, "VALUE")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.stateId
}