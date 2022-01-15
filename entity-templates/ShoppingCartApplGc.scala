package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ShoppingCartApplGc extends StorableClass(ShoppingCartApplGc) {
	object values extends ValuesObject {
		val certId = new IntFieldValue(self, ShoppingCartApplGc.fields.certId)
		val orderId = new IntFieldValue(self, ShoppingCartApplGc.fields.orderId)
		val amount = new NullableDoubleFieldValue(self, ShoppingCartApplGc.fields.amount)
		val createdOn = new NullableLocalDateTimeFieldValue(self, ShoppingCartApplGc.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ShoppingCartApplGc.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, ShoppingCartApplGc.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ShoppingCartApplGc.fields.updatedBy)
		val remainingValue = new NullableDoubleFieldValue(self, ShoppingCartApplGc.fields.remainingValue)
	}
}

object ShoppingCartApplGc extends StorableObject[ShoppingCartApplGc] {
	val entityName: String = "SHOPPING_CART_APPL_GC"

	object fields extends FieldsObject {
		val certId = new IntDatabaseField(self, "CERT_ID")
		val orderId = new IntDatabaseField(self, "ORDER_ID")
		val amount = new NullableDoubleDatabaseField(self, "AMOUNT")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val remainingValue = new NullableDoubleDatabaseField(self, "REMAINING_VALUE")
	}

	def primaryKey: IntDatabaseField = fields.certId
}