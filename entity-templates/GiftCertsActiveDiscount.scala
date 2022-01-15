package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class GiftCertsActiveDiscount extends StorableClass(GiftCertsActiveDiscount) {
	object values extends ValuesObject {
		val typeId = new NullableIntFieldValue(self, GiftCertsActiveDiscount.fields.typeId)
		val instanceId = new IntFieldValue(self, GiftCertsActiveDiscount.fields.instanceId)
		val discountAmt = new NullableDoubleFieldValue(self, GiftCertsActiveDiscount.fields.discountAmt)
	}
}

object GiftCertsActiveDiscount extends StorableObject[GiftCertsActiveDiscount] {
	val entityName: String = "GIFT_CERTS_ACTIVE_DISCOUNTS"

	object fields extends FieldsObject {
		val typeId = new NullableIntDatabaseField(self, "TYPE_ID")
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val discountAmt = new NullableDoubleDatabaseField(self, "DISCOUNT_AMT")
	}
}