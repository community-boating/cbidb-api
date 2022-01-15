package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpClassInstance extends StorableClass(JpClassInstance) {
	object values extends ValuesObject {
		val instanceId = new IntFieldValue(self, JpClassInstance.fields.instanceId)
		val typeId = new IntFieldValue(self, JpClassInstance.fields.typeId)
		val limitOverride = new NullableDoubleFieldValue(self, JpClassInstance.fields.limitOverride)
		val nameOverride = new NullableStringFieldValue(self, JpClassInstance.fields.nameOverride)
		val maxAge = new NullableDoubleFieldValue(self, JpClassInstance.fields.maxAge)
		val createdOn = new NullableLocalDateTimeFieldValue(self, JpClassInstance.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpClassInstance.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, JpClassInstance.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpClassInstance.fields.updatedBy)
		val minAge = new NullableDoubleFieldValue(self, JpClassInstance.fields.minAge)
		val price = new NullableDoubleFieldValue(self, JpClassInstance.fields.price)
		val regCodeRowId = new NullableIntFieldValue(self, JpClassInstance.fields.regCodeRowId)
		val confirmTemplate = new NullableStringFieldValue(self, JpClassInstance.fields.confirmTemplate)
		val adminHold = new NullableBooleanFIeldValue(self, JpClassInstance.fields.adminHold)
		val locationId = new NullableIntFieldValue(self, JpClassInstance.fields.locationId)
		val instructorId = new NullableIntFieldValue(self, JpClassInstance.fields.instructorId)
		val reservedForGroup = new NullableBooleanFIeldValue(self, JpClassInstance.fields.reservedForGroup)
		val overrideNoLimit = new NullableBooleanFIeldValue(self, JpClassInstance.fields.overrideNoLimit)
	}
}

object JpClassInstance extends StorableObject[JpClassInstance] {
	val entityName: String = "JP_CLASS_INSTANCES"

	object fields extends FieldsObject {
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val limitOverride = new NullableDoubleDatabaseField(self, "LIMIT_OVERRIDE")
		val nameOverride = new NullableStringDatabaseField(self, "NAME_OVERRIDE", 200)
		val maxAge = new NullableDoubleDatabaseField(self, "MAX_AGE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val minAge = new NullableDoubleDatabaseField(self, "MIN_AGE")
		val price = new NullableDoubleDatabaseField(self, "PRICE")
		val regCodeRowId = new NullableIntDatabaseField(self, "REG_CODE_ROW_ID")
		val confirmTemplate = new NullableStringDatabaseField(self, "CONFIRM_TEMPLATE", 100)
		val adminHold = new NullableBooleanDatabaseField(self, "ADMIN_HOLD")
		val locationId = new NullableIntDatabaseField(self, "LOCATION_ID")
		val instructorId = new NullableIntDatabaseField(self, "INSTRUCTOR_ID")
		val reservedForGroup = new NullableBooleanDatabaseField(self, "RESERVED_FOR_GROUP")
		val overrideNoLimit = new NullableBooleanDatabaseField(self, "OVERRIDE_NO_LIMIT")
	}

	def primaryKey: IntDatabaseField = fields.instanceId
}