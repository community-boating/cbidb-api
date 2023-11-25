package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class OrdersPromo extends StorableClass(OrdersPromo) {
	override object values extends ValuesObject {
		val assignId = new IntFieldValue(self, OrdersPromo.fields.assignId)
		val orderId = new IntFieldValue(self, OrdersPromo.fields.orderId)
		val instanceId = new IntFieldValue(self, OrdersPromo.fields.instanceId)
		val createdOn = new DateTimeFieldValue(self, OrdersPromo.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, OrdersPromo.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, OrdersPromo.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, OrdersPromo.fields.updatedBy)
	}
}

object OrdersPromo extends StorableObject[OrdersPromo] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "ORDERS_PROMOS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		@NullableInDatabase
		val orderId = new IntDatabaseField(self, "ORDER_ID")
		@NullableInDatabase
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.assignId
}