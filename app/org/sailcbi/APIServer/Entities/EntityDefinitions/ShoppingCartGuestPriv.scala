package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class ShoppingCartGuestPriv extends StorableClass(ShoppingCartGuestPriv) {
	override object values extends ValuesObject {
		val itemId = new IntFieldValue(self, ShoppingCartGuestPriv.fields.itemId)
		val orderId = new NullableIntFieldValue(self, ShoppingCartGuestPriv.fields.orderId)
		val price = new NullableDoubleFieldValue(self, ShoppingCartGuestPriv.fields.price)
		val createdOn = new DateTimeFieldValue(self, ShoppingCartGuestPriv.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ShoppingCartGuestPriv.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, ShoppingCartGuestPriv.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ShoppingCartGuestPriv.fields.updatedBy)
		val scMembershipId = new NullableIntFieldValue(self, ShoppingCartGuestPriv.fields.scMembershipId)
		val realMembershipId = new NullableIntFieldValue(self, ShoppingCartGuestPriv.fields.realMembershipId)
	}
}

object ShoppingCartGuestPriv extends StorableObject[ShoppingCartGuestPriv] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "SHOPPING_CART_GUEST_PRIVS"

	object fields extends FieldsObject {
		val itemId = new IntDatabaseField(self, "ITEM_ID")
		val orderId = new NullableIntDatabaseField(self, "ORDER_ID")
		val price = new NullableDoubleDatabaseField(self, "PRICE")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val scMembershipId = new NullableIntDatabaseField(self, "SC_MEMBERSHIP_ID")
		val realMembershipId = new NullableIntDatabaseField(self, "REAL_MEMBERSHIP_ID")
	}

	def primaryKey: IntDatabaseField = fields.itemId
}