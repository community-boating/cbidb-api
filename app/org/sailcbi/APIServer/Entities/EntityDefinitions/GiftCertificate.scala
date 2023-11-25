package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class GiftCertificate extends StorableClass(GiftCertificate) {
	override object values extends ValuesObject {
		val certId = new IntFieldValue(self, GiftCertificate.fields.certId)
		val certNumber = new StringFieldValue(self, GiftCertificate.fields.certNumber)
		val createdOn = new DateTimeFieldValue(self, GiftCertificate.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, GiftCertificate.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, GiftCertificate.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, GiftCertificate.fields.updatedBy)
		val redemptionHash = new NullableStringFieldValue(self, GiftCertificate.fields.redemptionHash)
		val redemptionCode = new NullableStringFieldValue(self, GiftCertificate.fields.redemptionCode)
	}
}

object GiftCertificate extends StorableObject[GiftCertificate] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "GIFT_CERTIFICATES"

	object fields extends FieldsObject {
		val certId = new IntDatabaseField(self, "CERT_ID")
		val certNumber = new StringDatabaseField(self, "CERT_NUMBER", 50)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val redemptionHash = new NullableStringDatabaseField(self, "REDEMPTION_HASH", 50)
		val redemptionCode = new NullableStringDatabaseField(self, "REDEMPTION_CODE", 20)
	}

	def primaryKey: IntDatabaseField = fields.certId
}