package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class DiscountMapping extends StorableClass(DiscountMapping) {
	object values extends ValuesObject {
		val oldDiscountId = new IntFieldValue(self, DiscountMapping.fields.oldDiscountId)
		val instanceId = new NullableIntFieldValue(self, DiscountMapping.fields.instanceId)
	}
}

object DiscountMapping extends StorableObject[DiscountMapping] {
	val entityName: String = "DISCOUNT_MAPPING"

	object fields extends FieldsObject {
		val oldDiscountId = new IntDatabaseField(self, "OLD_DISCOUNT_ID")
		val instanceId = new NullableIntDatabaseField(self, "INSTANCE_ID")
	}

	def primaryKey: IntDatabaseField = fields.oldDiscountId
}