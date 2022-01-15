package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class GiftCertsAvailableDiscount extends StorableClass(GiftCertsAvailableDiscount) {
	object values extends ValuesObject {
		val typeId = new NullableIntFieldValue(self, GiftCertsAvailableDiscount.fields.typeId)
		val instanceId = new IntFieldValue(self, GiftCertsAvailableDiscount.fields.instanceId)
		val discountAmt = new NullableDoubleFieldValue(self, GiftCertsAvailableDiscount.fields.discountAmt)
	}
}

object GiftCertsAvailableDiscount extends StorableObject[GiftCertsAvailableDiscount] {
	val entityName: String = "GIFT_CERTS_AVAILABLE_DISCOUNTS"

	object fields extends FieldsObject {
		val typeId = new NullableIntDatabaseField(self, "TYPE_ID")
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val discountAmt = new NullableDoubleDatabaseField(self, "DISCOUNT_AMT")
	}
}