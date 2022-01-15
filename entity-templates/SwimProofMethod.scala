package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class SwimProofMethod extends StorableClass(SwimProofMethod) {
	object values extends ValuesObject {
		val methodId = new IntFieldValue(self, SwimProofMethod.fields.methodId)
		val methodName = new StringFieldValue(self, SwimProofMethod.fields.methodName)
		val active = new NullableBooleanFIeldValue(self, SwimProofMethod.fields.active)
		val displayOrder = new NullableDoubleFieldValue(self, SwimProofMethod.fields.displayOrder)
		val createdOn = new NullableLocalDateTimeFieldValue(self, SwimProofMethod.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, SwimProofMethod.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, SwimProofMethod.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, SwimProofMethod.fields.updatedBy)
	}
}

object SwimProofMethod extends StorableObject[SwimProofMethod] {
	val entityName: String = "SWIM_PROOF_METHODS"

	object fields extends FieldsObject {
		val methodId = new IntDatabaseField(self, "METHOD_ID")
		val methodName = new StringDatabaseField(self, "METHOD_NAME", 4000)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.methodId
}