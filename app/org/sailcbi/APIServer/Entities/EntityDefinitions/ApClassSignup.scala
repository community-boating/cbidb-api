package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.framework.Storable.FieldValues.{IntFieldValue, StringFieldValue}
import com.coleji.framework.Storable.Fields.{IntDatabaseField, StringDatabaseField}
import com.coleji.framework.Storable._
import org.sailcbi.APIServer.CbiUtil.Initializable

class ApClassSignup extends StorableClass {
	this.setCompanion(ApClassSignup)

	object references extends ReferencesObject {
		var apClassInstance = new Initializable[ApClassSignup]
	}

	object values extends ValuesObject {
		val signupId = new IntFieldValue(self, ApClassSignup.fields.signupId)
		val instanceId = new IntFieldValue(self, ApClassSignup.fields.instanceId)
		val signupType = new StringFieldValue(self, ApClassSignup.fields.signupType)
	}

	override val valuesList = List(
		values.signupId,
		values.instanceId,
		values.signupType
	)
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