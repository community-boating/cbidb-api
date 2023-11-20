package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class UapGroupPrice extends StorableClass(UapGroupPrice) {
	override object values extends ValuesObject {
		val priceId = new IntFieldValue(self, UapGroupPrice.fields.priceId)
		val uapGroupId = new NullableIntFieldValue(self, UapGroupPrice.fields.uapGroupId)
		val unitPrice = new NullableDoubleFieldValue(self, UapGroupPrice.fields.unitPrice)
		val createdOn = new DateTimeFieldValue(self, UapGroupPrice.fields.createdOn)
		val createdBy = new StringFieldValue(self, UapGroupPrice.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, UapGroupPrice.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, UapGroupPrice.fields.updatedBy)
		val effectiveDatetime = new NullableDateTimeFieldValue(self, UapGroupPrice.fields.effectiveDatetime)
	}
}

object UapGroupPrice extends StorableObject[UapGroupPrice] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "UAP_GROUP_PRICES"

	object fields extends FieldsObject {
		val priceId = new IntDatabaseField(self, "PRICE_ID")
		val uapGroupId = new NullableIntDatabaseField(self, "UAP_GROUP_ID")
		val unitPrice = new NullableDoubleDatabaseField(self, "UNIT_PRICE")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val effectiveDatetime = new NullableDateTimeDatabaseField(self, "EFFECTIVE_DATETIME")
	}

	def primaryKey: IntDatabaseField = fields.priceId
}