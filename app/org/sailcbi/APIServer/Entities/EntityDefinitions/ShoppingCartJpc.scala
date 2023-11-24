package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class ShoppingCartJpc extends StorableClass(ShoppingCartJpc) {
	override object values extends ValuesObject {
		val itemId = new IntFieldValue(self, ShoppingCartJpc.fields.itemId)
		val personId = new NullableIntFieldValue(self, ShoppingCartJpc.fields.personId)
		val orderId = new NullableIntFieldValue(self, ShoppingCartJpc.fields.orderId)
		val instanceId = new NullableIntFieldValue(self, ShoppingCartJpc.fields.instanceId)
		val readyToBuy = new NullableStringFieldValue(self, ShoppingCartJpc.fields.readyToBuy)
		val createdOn = new DateTimeFieldValue(self, ShoppingCartJpc.fields.createdOn)
		val createdBy = new StringFieldValue(self, ShoppingCartJpc.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, ShoppingCartJpc.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, ShoppingCartJpc.fields.updatedBy)
		val price = new NullableDoubleFieldValue(self, ShoppingCartJpc.fields.price)
		val discountInstanceId = new NullableIntFieldValue(self, ShoppingCartJpc.fields.discountInstanceId)
		val discountAmt = new NullableDoubleFieldValue(self, ShoppingCartJpc.fields.discountAmt)
	}
}

object ShoppingCartJpc extends StorableObject[ShoppingCartJpc] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "SHOPPING_CART_JPC"

	object fields extends FieldsObject {
		val itemId = new IntDatabaseField(self, "ITEM_ID")
		val personId = new NullableIntDatabaseField(self, "PERSON_ID")
		val orderId = new NullableIntDatabaseField(self, "ORDER_ID")
		val instanceId = new NullableIntDatabaseField(self, "INSTANCE_ID")
		val readyToBuy = new NullableStringDatabaseField(self, "READY_TO_BUY", 1)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val price = new NullableDoubleDatabaseField(self, "PRICE")
		val discountInstanceId = new NullableIntDatabaseField(self, "DISCOUNT_INSTANCE_ID")
		val discountAmt = new NullableDoubleDatabaseField(self, "DISCOUNT_AMT")
	}

	def primaryKey: IntDatabaseField = fields.itemId
}