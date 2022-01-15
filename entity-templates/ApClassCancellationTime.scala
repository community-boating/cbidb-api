package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ApClassCancellationTime extends StorableClass(ApClassCancellationTime) {
	object values extends ValuesObject {
		val doNotAutoCancel = new NullableBooleanFIeldValue(self, ApClassCancellationTime.fields.doNotAutoCancel)
		val typeId = new IntFieldValue(self, ApClassCancellationTime.fields.typeId)
		val instanceId = new IntFieldValue(self, ApClassCancellationTime.fields.instanceId)
		val sessionDatetime = new LocalDateTimeFieldValue(self, ApClassCancellationTime.fields.sessionDatetime)
		val typeName = new StringFieldValue(self, ApClassCancellationTime.fields.typeName)
		val signupMin = new NullableDoubleFieldValue(self, ApClassCancellationTime.fields.signupMin)
		val enrolledCt = new NullableDoubleFieldValue(self, ApClassCancellationTime.fields.enrolledCt)
		val wlCt = new NullableDoubleFieldValue(self, ApClassCancellationTime.fields.wlCt)
		val daysUntil = new NullableDoubleFieldValue(self, ApClassCancellationTime.fields.daysUntil)
	}
}

object ApClassCancellationTime extends StorableObject[ApClassCancellationTime] {
	val entityName: String = "AP_CLASS_CANCELLATION_TIMES"

	object fields extends FieldsObject {
		val doNotAutoCancel = new NullableBooleanDatabaseField(self, "DO_NOT_AUTO_CANCEL")
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val sessionDatetime = new LocalDateTimeDatabaseField(self, "SESSION_DATETIME")
		val typeName = new StringDatabaseField(self, "TYPE_NAME", 200)
		val signupMin = new NullableDoubleDatabaseField(self, "SIGNUP_MIN")
		val enrolledCt = new NullableDoubleDatabaseField(self, "ENROLLED_CT")
		val wlCt = new NullableDoubleDatabaseField(self, "WL_CT")
		val daysUntil = new NullableDoubleDatabaseField(self, "DAYS_UNTIL")
	}
}