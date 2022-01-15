package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class DiscountActiveInstance extends StorableClass(DiscountActiveInstance) {
	object values extends ValuesObject {
		val discountId = new IntFieldValue(self, DiscountActiveInstance.fields.discountId)
		val instanceId = new NullableIntFieldValue(self, DiscountActiveInstance.fields.instanceId)
	}
}

object DiscountActiveInstance extends StorableObject[DiscountActiveInstance] {
	val entityName: String = "DISCOUNT_ACTIVE_INSTANCES"

	object fields extends FieldsObject {
		val discountId = new IntDatabaseField(self, "DISCOUNT_ID")
		val instanceId = new NullableIntDatabaseField(self, "INSTANCE_ID")
	}
}