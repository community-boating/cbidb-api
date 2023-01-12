package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ApClassInstance extends StorableClass(ApClassInstance) {
	override object references extends ReferencesObject {
		val apClassFormat = new Initializable[ApClassFormat]
		val apClassSessions = new Initializable[List[ApClassSession]]
		val apClassSignups = new Initializable[List[ApClassSignup]]
	}

	object values extends ValuesObject {
		val instanceId = new IntFieldValue(self, ApClassInstance.fields.instanceId)
		val signupsStartOverride = new NullableDateTimeFieldValue(self, ApClassInstance.fields.signupsStartOverride)
		val signupMin = new NullableIntFieldValue(self, ApClassInstance.fields.signupMin)
		val signupMax = new NullableIntFieldValue(self, ApClassInstance.fields.signupMax)
		val formatId = new IntFieldValue(self, ApClassInstance.fields.formatId)
		val cancelByOverride = new NullableDateTimeFieldValue(self, ApClassInstance.fields.cancelByOverride)
		val price = new NullableDoubleFieldValue(self, ApClassInstance.fields.price)
		val cancelledDatetime = new NullableDateTimeFieldValue(self, ApClassInstance.fields.cancelledDatetime)
		val hideOnline = new BooleanFieldValue(self, ApClassInstance.fields.hideOnline)
		val locationString = new NullableStringFieldValue(self, ApClassInstance.fields.locationString)
	}
}

object ApClassInstance extends StorableObject[ApClassInstance] {
	val entityName: String = "AP_CLASS_INSTANCES"

	override val useRuntimeFieldnamesForJson: Boolean = true

	object fields extends FieldsObject {
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val signupsStartOverride = new NullableDateTimeDatabaseField(self, "SIGNUPS_START_OVERRIDE")
		val signupMin = new NullableIntDatabaseField(self, "SIGNUP_MIN")
		val signupMax = new NullableIntDatabaseField(self, "SIGNUP_MAX")
		val formatId = new IntDatabaseField(self, "FORMAT_ID")
		val cancelByOverride = new NullableDateTimeDatabaseField(self, "CANCEL_BY_OVERRIDE")
		val price = new NullableDoubleDatabaseField(self, "PRICE")
		val cancelledDatetime = new NullableDateTimeDatabaseField(self, "CANCELLED_DATETIME")
		val hideOnline = new BooleanDatabaseField(self, "HIDE_ONLINE", true)
		val locationString = new NullableStringDatabaseField(self, "LOCATION_STRING", 100)
	}

	def primaryKey: IntDatabaseField = fields.instanceId
}
