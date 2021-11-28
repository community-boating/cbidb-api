package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ShoppingCartMembership extends StorableClass(ShoppingCartMembership) {
	object values extends ValuesObject {
		val itemId = new IntFieldValue(self, ShoppingCartMembership.fields.itemId)
		val membershipTypeId = new NullableIntFieldValue(self, ShoppingCartMembership.fields.membershipTypeId)
		val price = new DoubleFieldValue(self, ShoppingCartMembership.fields.price)
		val createdOn = new NullableLocalDateTimeFieldValue(self, ShoppingCartMembership.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ShoppingCartMembership.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, ShoppingCartMembership.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ShoppingCartMembership.fields.updatedBy)
		val personId = new IntFieldValue(self, ShoppingCartMembership.fields.personId)
		val readyToBuy = new BooleanFIeldValue(self, ShoppingCartMembership.fields.readyToBuy)
		val orderId = new IntFieldValue(self, ShoppingCartMembership.fields.orderId)
		val specNeedsApproved = new NullableBooleanFIeldValue(self, ShoppingCartMembership.fields.specNeedsApproved)
		val discountAmt = new NullableDoubleFieldValue(self, ShoppingCartMembership.fields.discountAmt)
		val discountInstanceId = new NullableIntFieldValue(self, ShoppingCartMembership.fields.discountInstanceId)
		val discountId = new NullableIntFieldValue(self, ShoppingCartMembership.fields.discountId)
		val hasJpClassReservations = new NullableBooleanFIeldValue(self, ShoppingCartMembership.fields.hasJpClassReservations)
		val requestedDiscountInstanceId = new NullableIntFieldValue(self, ShoppingCartMembership.fields.requestedDiscountInstanceId)
	}
}

object ShoppingCartMembership extends StorableObject[ShoppingCartMembership] {
	val entityName: String = "SHOPPING_CART_MEMBERSHIPS"

	object fields extends FieldsObject {
		val itemId = new IntDatabaseField(self, "ITEM_ID")
		val membershipTypeId = new NullableIntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
		val price = new DoubleDatabaseField(self, "PRICE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val readyToBuy = new BooleanDatabaseField(self, "READY_TO_BUY")
		val orderId = new IntDatabaseField(self, "ORDER_ID")
		val specNeedsApproved = new NullableBooleanDatabaseField(self, "SPEC_NEEDS_APPROVED")
		val discountAmt = new NullableDoubleDatabaseField(self, "DISCOUNT_AMT")
		val discountInstanceId = new NullableIntDatabaseField(self, "DISCOUNT_INSTANCE_ID")
		val discountId = new NullableIntDatabaseField(self, "DISCOUNT_ID")
		val hasJpClassReservations = new NullableBooleanDatabaseField(self, "HAS_JP_CLASS_RESERVATIONS")
		val requestedDiscountInstanceId = new NullableIntDatabaseField(self, "REQUESTED_DISCOUNT_INSTANCE_ID")
	}

	def primaryKey: IntDatabaseField = fields.itemId
}