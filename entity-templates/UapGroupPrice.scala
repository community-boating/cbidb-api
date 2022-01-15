package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class UapGroupPrice extends StorableClass(UapGroupPrice) {
	object values extends ValuesObject {
		val priceId = new IntFieldValue(self, UapGroupPrice.fields.priceId)
		val uapGroupId = new NullableIntFieldValue(self, UapGroupPrice.fields.uapGroupId)
		val effectiveDatetime = new NullableLocalDateTimeFieldValue(self, UapGroupPrice.fields.effectiveDatetime)
		val unitPrice = new NullableDoubleFieldValue(self, UapGroupPrice.fields.unitPrice)
		val createdOn = new NullableLocalDateTimeFieldValue(self, UapGroupPrice.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, UapGroupPrice.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, UapGroupPrice.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, UapGroupPrice.fields.updatedBy)
	}
}

object UapGroupPrice extends StorableObject[UapGroupPrice] {
	val entityName: String = "UAP_GROUP_PRICES"

	object fields extends FieldsObject {
		val priceId = new IntDatabaseField(self, "PRICE_ID")
		val uapGroupId = new NullableIntDatabaseField(self, "UAP_GROUP_ID")
		val effectiveDatetime = new NullableLocalDateTimeDatabaseField(self, "EFFECTIVE_DATETIME")
		val unitPrice = new NullableDoubleDatabaseField(self, "UNIT_PRICE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.priceId
}