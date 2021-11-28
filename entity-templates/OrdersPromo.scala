package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class OrdersPromo extends StorableClass(OrdersPromo) {
	object values extends ValuesObject {
		val assignId = new IntFieldValue(self, OrdersPromo.fields.assignId)
		val orderId = new NullableIntFieldValue(self, OrdersPromo.fields.orderId)
		val instanceId = new NullableIntFieldValue(self, OrdersPromo.fields.instanceId)
		val createdOn = new NullableLocalDateTimeFieldValue(self, OrdersPromo.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, OrdersPromo.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, OrdersPromo.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, OrdersPromo.fields.updatedBy)
	}
}

object OrdersPromo extends StorableObject[OrdersPromo] {
	val entityName: String = "ORDERS_PROMOS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val orderId = new NullableIntDatabaseField(self, "ORDER_ID")
		val instanceId = new NullableIntDatabaseField(self, "INSTANCE_ID")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.assignId
}