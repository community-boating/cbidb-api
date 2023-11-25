package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class DiscountMapping extends StorableClass(DiscountMapping) {
	override object values extends ValuesObject {
		val oldDiscountId = new IntFieldValue(self, DiscountMapping.fields.oldDiscountId)
		val instanceId = new IntFieldValue(self, DiscountMapping.fields.instanceId)
	}
}

object DiscountMapping extends StorableObject[DiscountMapping] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "DISCOUNT_MAPPING"

	object fields extends FieldsObject {
		val oldDiscountId = new IntDatabaseField(self, "OLD_DISCOUNT_ID")
		@NullableInDatabase
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
	}

	def primaryKey: IntDatabaseField = fields.oldDiscountId
}