package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class JpClassInstance extends StorableClass(JpClassInstance) {
	override object references extends ReferencesObject {
		val classLocation = new Initializable[Option[ClassLocation]]
		val classInstructor = new Initializable[Option[ClassInstructor]]
		val jpClassType = new Initializable[JpClassType]
		val jpClassSessions = new Initializable[IndexedSeq[JpClassSession]]
		val jpClassSignups = new Initializable[IndexedSeq[JpClassSignup]]
	}

	override object values extends ValuesObject {
		val instanceId = new IntFieldValue(self, JpClassInstance.fields.instanceId)
		val typeId = new IntFieldValue(self, JpClassInstance.fields.typeId)
		val limitOverride = new NullableDoubleFieldValue(self, JpClassInstance.fields.limitOverride)
		val nameOverride = new NullableStringFieldValue(self, JpClassInstance.fields.nameOverride)
		val maxAge = new NullableDoubleFieldValue(self, JpClassInstance.fields.maxAge)
		val createdOn = new DateTimeFieldValue(self, JpClassInstance.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpClassInstance.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, JpClassInstance.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpClassInstance.fields.updatedBy)
		val minAge = new NullableDoubleFieldValue(self, JpClassInstance.fields.minAge)
		val price = new NullableDoubleFieldValue(self, JpClassInstance.fields.price)
		val regCodeRowId = new NullableIntFieldValue(self, JpClassInstance.fields.regCodeRowId)
		val confirmTemplate = new NullableStringFieldValue(self, JpClassInstance.fields.confirmTemplate)
		val adminHold = new NullableBooleanFieldValue(self, JpClassInstance.fields.adminHold)
		val instructorId = new NullableIntFieldValue(self, JpClassInstance.fields.instructorId)
		val locationId = new NullableIntFieldValue(self, JpClassInstance.fields.locationId)
		val reservedForGroup = new NullableBooleanFieldValue(self, JpClassInstance.fields.reservedForGroup)
		val overrideNoLimit = new NullableBooleanFieldValue(self, JpClassInstance.fields.overrideNoLimit)
	}
}

object JpClassInstance extends StorableObject[JpClassInstance] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "JP_CLASS_INSTANCES"

	object fields extends FieldsObject {
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val limitOverride = new NullableDoubleDatabaseField(self, "LIMIT_OVERRIDE")
		val nameOverride = new NullableStringDatabaseField(self, "NAME_OVERRIDE", 200)
		val maxAge = new NullableDoubleDatabaseField(self, "MAX_AGE")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val minAge = new NullableDoubleDatabaseField(self, "MIN_AGE")
		val price = new NullableDoubleDatabaseField(self, "PRICE")
		val regCodeRowId = new NullableIntDatabaseField(self, "REG_CODE_ROW_ID")
		val confirmTemplate = new NullableStringDatabaseField(self, "CONFIRM_TEMPLATE", 100)
		val adminHold = new NullableBooleanDatabaseField(self, "ADMIN_HOLD")
		val instructorId = new NullableIntDatabaseField(self, "INSTRUCTOR_ID")
		val locationId = new NullableIntDatabaseField(self, "LOCATION_ID")
		val reservedForGroup = new NullableBooleanDatabaseField(self, "RESERVED_FOR_GROUP")
		val overrideNoLimit = new NullableBooleanDatabaseField(self, "OVERRIDE_NO_LIMIT")
	}

	def primaryKey: IntDatabaseField = fields.instanceId
}