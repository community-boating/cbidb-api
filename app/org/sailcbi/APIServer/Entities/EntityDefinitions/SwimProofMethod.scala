package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class SwimProofMethod extends StorableClass(SwimProofMethod) {
	override object values extends ValuesObject {
		val methodId = new IntFieldValue(self, SwimProofMethod.fields.methodId)
		val methodName = new StringFieldValue(self, SwimProofMethod.fields.methodName)
		val active = new BooleanFieldValue(self, SwimProofMethod.fields.active)
		val displayOrder = new DoubleFieldValue(self, SwimProofMethod.fields.displayOrder)
		val createdOn = new DateTimeFieldValue(self, SwimProofMethod.fields.createdOn)
		val createdBy = new StringFieldValue(self, SwimProofMethod.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, SwimProofMethod.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, SwimProofMethod.fields.updatedBy)
	}
}

object SwimProofMethod extends StorableObject[SwimProofMethod] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "SWIM_PROOF_METHODS"

	object fields extends FieldsObject {
		val methodId = new IntDatabaseField(self, "METHOD_ID")
		val methodName = new StringDatabaseField(self, "METHOD_NAME", 4000)
		@NullableInDatabase
		val active = new BooleanDatabaseField(self, "ACTIVE", false)
		@NullableInDatabase
		val displayOrder = new DoubleDatabaseField(self, "DISPLAY_ORDER")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.methodId
}