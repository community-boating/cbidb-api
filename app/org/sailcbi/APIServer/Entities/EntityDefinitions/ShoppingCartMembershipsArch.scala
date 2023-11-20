package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ShoppingCartMembershipsArch extends StorableClass(ShoppingCartMembershipsArch) {
	override object values extends ValuesObject {
		val itemId = new NullableIntFieldValue(self, ShoppingCartMembershipsArch.fields.itemId)
		val membershipTypeId = new NullableIntFieldValue(self, ShoppingCartMembershipsArch.fields.membershipTypeId)
		val price = new DoubleFieldValue(self, ShoppingCartMembershipsArch.fields.price)
		val createdOn = new DateTimeFieldValue(self, ShoppingCartMembershipsArch.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ShoppingCartMembershipsArch.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, ShoppingCartMembershipsArch.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ShoppingCartMembershipsArch.fields.updatedBy)
		val personId = new IntFieldValue(self, ShoppingCartMembershipsArch.fields.personId)
		val readyToBuy = new StringFieldValue(self, ShoppingCartMembershipsArch.fields.readyToBuy)
		val orderId = new IntFieldValue(self, ShoppingCartMembershipsArch.fields.orderId)
		val specNeedsApproved = new NullableBooleanFieldValue(self, ShoppingCartMembershipsArch.fields.specNeedsApproved)
		val discountAmt = new NullableDoubleFieldValue(self, ShoppingCartMembershipsArch.fields.discountAmt)
		val discountId = new NullableIntFieldValue(self, ShoppingCartMembershipsArch.fields.discountId)
		val discountInstanceId = new NullableIntFieldValue(self, ShoppingCartMembershipsArch.fields.discountInstanceId)
		val hasJpClassReservations = new NullableBooleanFieldValue(self, ShoppingCartMembershipsArch.fields.hasJpClassReservations)
		val requestedDiscountInstanceId = new NullableIntFieldValue(self, ShoppingCartMembershipsArch.fields.requestedDiscountInstanceId)
	}
}

object ShoppingCartMembershipsArch extends StorableObject[ShoppingCartMembershipsArch] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "SHOPPING_CART_MEMBERSHIPS_ARCH"

	object fields extends FieldsObject {
		val itemId = new NullableIntDatabaseField(self, "ITEM_ID")
		val membershipTypeId = new NullableIntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
		val price = new DoubleDatabaseField(self, "PRICE")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val readyToBuy = new StringDatabaseField(self, "READY_TO_BUY", 1)
		val orderId = new IntDatabaseField(self, "ORDER_ID")
		val specNeedsApproved = new NullableBooleanDatabaseField(self, "SPEC_NEEDS_APPROVED")
		val discountAmt = new NullableDoubleDatabaseField(self, "DISCOUNT_AMT")
		val discountId = new NullableIntDatabaseField(self, "DISCOUNT_ID")
		val discountInstanceId = new NullableIntDatabaseField(self, "DISCOUNT_INSTANCE_ID")
		val hasJpClassReservations = new NullableBooleanDatabaseField(self, "HAS_JP_CLASS_RESERVATIONS")
		val requestedDiscountInstanceId = new NullableIntDatabaseField(self, "REQUESTED_DISCOUNT_INSTANCE_ID")
	}
}