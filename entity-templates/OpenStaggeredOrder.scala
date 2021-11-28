package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class OpenStaggeredOrder extends StorableClass(OpenStaggeredOrder) {
	object values extends ValuesObject {
		val orderId = new NullableIntFieldValue(self, OpenStaggeredOrder.fields.orderId)
		val firstPayment = new NullableLocalDateTimeFieldValue(self, OpenStaggeredOrder.fields.firstPayment)
		val lastPayment = new NullableLocalDateTimeFieldValue(self, OpenStaggeredOrder.fields.lastPayment)
	}
}

object OpenStaggeredOrder extends StorableObject[OpenStaggeredOrder] {
	val entityName: String = "OPEN_STAGGERED_ORDERS"

	object fields extends FieldsObject {
		val orderId = new NullableIntDatabaseField(self, "ORDER_ID")
		val firstPayment = new NullableLocalDateTimeDatabaseField(self, "FIRST_PAYMENT")
		val lastPayment = new NullableLocalDateTimeDatabaseField(self, "LAST_PAYMENT")
	}
}