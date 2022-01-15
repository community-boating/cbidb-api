package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class GiftCertificate extends StorableClass(GiftCertificate) {
	object values extends ValuesObject {
		val certId = new IntFieldValue(self, GiftCertificate.fields.certId)
		val certNumber = new StringFieldValue(self, GiftCertificate.fields.certNumber)
		val createdOn = new NullableLocalDateTimeFieldValue(self, GiftCertificate.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, GiftCertificate.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, GiftCertificate.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, GiftCertificate.fields.updatedBy)
		val redemptionHash = new NullableStringFieldValue(self, GiftCertificate.fields.redemptionHash)
		val redemptionCode = new NullableStringFieldValue(self, GiftCertificate.fields.redemptionCode)
	}
}

object GiftCertificate extends StorableObject[GiftCertificate] {
	val entityName: String = "GIFT_CERTIFICATES"

	object fields extends FieldsObject {
		val certId = new IntDatabaseField(self, "CERT_ID")
		val certNumber = new StringDatabaseField(self, "CERT_NUMBER", 50)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val redemptionHash = new NullableStringDatabaseField(self, "REDEMPTION_HASH", 50)
		val redemptionCode = new NullableStringDatabaseField(self, "REDEMPTION_CODE", 20)
	}

	def primaryKey: IntDatabaseField = fields.certId
}