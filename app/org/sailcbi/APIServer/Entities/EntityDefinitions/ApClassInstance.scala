package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class ApClassInstance extends StorableClass(ApClassInstance) {
	override object references extends ReferencesObject {
		val apClassFormat = new Initializable[ApClassFormat]
		val apClassSessions = new InitializableSeq[ApClassSession, IndexedSeq[ApClassSession]]
		val apClassSignups = new InitializableSeq[ApClassSignup, IndexedSeq[ApClassSignup]]
		val instructor = new Initializable[Option[Person]]
	}

	override object values extends ValuesObject {
		val instanceId = new IntFieldValue(self, ApClassInstance.fields.instanceId)
		val createdOn = new DateTimeFieldValue(self, ApClassInstance.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ApClassInstance.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, ApClassInstance.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ApClassInstance.fields.updatedBy)
		val cancelByOverride = new NullableDateTimeFieldValue(self, ApClassInstance.fields.cancelByOverride)
		val formatId = new IntFieldValue(self, ApClassInstance.fields.formatId)
		val signupsStartOverride = new NullableDateTimeFieldValue(self, ApClassInstance.fields.signupsStartOverride)
		val cancelledDatetime = new NullableDateTimeFieldValue(self, ApClassInstance.fields.cancelledDatetime)
		val price = new NullableDoubleFieldValue(self, ApClassInstance.fields.price)
		val signupMax = new NullableIntFieldValue(self, ApClassInstance.fields.signupMax)
		val signupMin = new NullableIntFieldValue(self, ApClassInstance.fields.signupMin)
		val doNotAutoCancel = new BooleanFieldValue(self, ApClassInstance.fields.doNotAutoCancel)
		val hideOnline = new BooleanFieldValue(self, ApClassInstance.fields.hideOnline)
		val locationString = new NullableStringFieldValue(self, ApClassInstance.fields.locationString)
		val instructorId = new NullableIntFieldValue(self, ApClassInstance.fields.instructorId)
	}
}

object ApClassInstance extends StorableObject[ApClassInstance] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "AP_CLASS_INSTANCES"

	object fields extends FieldsObject {
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val cancelByOverride = new NullableDateTimeDatabaseField(self, "CANCEL_BY_OVERRIDE")
		val formatId = new IntDatabaseField(self, "FORMAT_ID")
		val signupsStartOverride = new NullableDateTimeDatabaseField(self, "SIGNUPS_START_OVERRIDE")
		val cancelledDatetime = new NullableDateTimeDatabaseField(self, "CANCELLED_DATETIME")
		val price = new NullableDoubleDatabaseField(self, "PRICE")
		val signupMax = new NullableIntDatabaseField(self, "SIGNUP_MAX")
		val signupMin = new NullableIntDatabaseField(self, "SIGNUP_MIN")
		@NullableInDatabase
		val doNotAutoCancel = new BooleanDatabaseField(self, "DO_NOT_AUTO_CANCEL", true)
		@NullableInDatabase
		val hideOnline = new BooleanDatabaseField(self, "HIDE_ONLINE", true)
		val locationString = new NullableStringDatabaseField(self, "LOCATION_STRING", 100)
		val instructorId = new NullableIntDatabaseField(self, "INSTRUCTOR_ID")
	}

	def primaryKey: IntDatabaseField = fields.instanceId
}