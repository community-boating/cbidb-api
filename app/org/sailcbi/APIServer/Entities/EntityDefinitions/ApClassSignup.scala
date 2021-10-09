package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues.{IntFieldValue, StringFieldValue}
import com.coleji.neptune.Storable.Fields.{IntDatabaseField, StringDatabaseField}
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ApClassSignup extends StorableClass(ApClassSignup) {
	override object references extends ReferencesObject {
		val apClassInstance = new Initializable[ApClassSignup]
	}

	object values extends ValuesObject {
		val signupId = new IntFieldValue(self, ApClassSignup.fields.signupId)
		val instanceId = new IntFieldValue(self, ApClassSignup.fields.instanceId)
		val signupType = new StringFieldValue(self, ApClassSignup.fields.signupType)
	}
}

object ApClassSignup extends StorableObject[ApClassSignup] {
	val entityName: String = "AP_CLASS_SIGNUPS"

	object fields extends FieldsObject {
		val signupId = new IntDatabaseField(self, "SIGNUP_ID")
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val signupType = new StringDatabaseField(self, "SIGNUP_TYPE", 1)
	}

	def primaryKey: IntDatabaseField = fields.signupId
}