package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class GiftCertsDiscount extends StorableClass(GiftCertsDiscount) {
	object values extends ValuesObject {
		val assignId = new IntFieldValue(self, GiftCertsDiscount.fields.assignId)
		val typeId = new NullableIntFieldValue(self, GiftCertsDiscount.fields.typeId)
		val instanceId = new IntFieldValue(self, GiftCertsDiscount.fields.instanceId)
		val discountAmt = new NullableDoubleFieldValue(self, GiftCertsDiscount.fields.discountAmt)
		val availableOnline = new NullableBooleanFIeldValue(self, GiftCertsDiscount.fields.availableOnline)
		val fixedCostThreshold = new NullableDoubleFieldValue(self, GiftCertsDiscount.fields.fixedCostThreshold)
		val limit = new NullableDoubleFieldValue(self, GiftCertsDiscount.fields.limit)
	}
}

object GiftCertsDiscount extends StorableObject[GiftCertsDiscount] {
	val entityName: String = "GIFT_CERTS_DISCOUNTS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val typeId = new NullableIntDatabaseField(self, "TYPE_ID")
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val discountAmt = new NullableDoubleDatabaseField(self, "DISCOUNT_AMT")
		val availableOnline = new NullableBooleanDatabaseField(self, "AVAILABLE_ONLINE")
		val fixedCostThreshold = new NullableDoubleDatabaseField(self, "FIXED_COST_THRESHOLD")
		val limit = new NullableDoubleDatabaseField(self, "LIMIT")
	}

	def primaryKey: IntDatabaseField = fields.assignId
}