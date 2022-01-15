package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ApClassInstance extends StorableClass(ApClassInstance) {
	object values extends ValuesObject {
		val instanceId = new IntFieldValue(self, ApClassInstance.fields.instanceId)
		val createdOn = new NullableLocalDateTimeFieldValue(self, ApClassInstance.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ApClassInstance.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, ApClassInstance.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ApClassInstance.fields.updatedBy)
		val signupsStartOverride = new NullableLocalDateTimeFieldValue(self, ApClassInstance.fields.signupsStartOverride)
		val signupMin = new NullableDoubleFieldValue(self, ApClassInstance.fields.signupMin)
		val signupMax = new NullableDoubleFieldValue(self, ApClassInstance.fields.signupMax)
		val formatId = new IntFieldValue(self, ApClassInstance.fields.formatId)
		val cancelByOverride = new NullableLocalDateTimeFieldValue(self, ApClassInstance.fields.cancelByOverride)
		val price = new NullableDoubleFieldValue(self, ApClassInstance.fields.price)
		val cancelledDatetime = new NullableLocalDateTimeFieldValue(self, ApClassInstance.fields.cancelledDatetime)
		val doNotAutoCancel = new NullableBooleanFIeldValue(self, ApClassInstance.fields.doNotAutoCancel)
		val hideOnline = new NullableBooleanFIeldValue(self, ApClassInstance.fields.hideOnline)
		val locationString = new NullableStringFieldValue(self, ApClassInstance.fields.locationString)
	}
}

object ApClassInstance extends StorableObject[ApClassInstance] {
	val entityName: String = "AP_CLASS_INSTANCES"

	object fields extends FieldsObject {
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val signupsStartOverride = new NullableLocalDateTimeDatabaseField(self, "SIGNUPS_START_OVERRIDE")
		val signupMin = new NullableDoubleDatabaseField(self, "SIGNUP_MIN")
		val signupMax = new NullableDoubleDatabaseField(self, "SIGNUP_MAX")
		val formatId = new IntDatabaseField(self, "FORMAT_ID")
		val cancelByOverride = new NullableLocalDateTimeDatabaseField(self, "CANCEL_BY_OVERRIDE")
		val price = new NullableDoubleDatabaseField(self, "PRICE")
		val cancelledDatetime = new NullableLocalDateTimeDatabaseField(self, "CANCELLED_DATETIME")
		val doNotAutoCancel = new NullableBooleanDatabaseField(self, "DO_NOT_AUTO_CANCEL")
		val hideOnline = new NullableBooleanDatabaseField(self, "HIDE_ONLINE")
		val locationString = new NullableStringDatabaseField(self, "LOCATION_STRING", 100)
	}

	def primaryKey: IntDatabaseField = fields.instanceId
}