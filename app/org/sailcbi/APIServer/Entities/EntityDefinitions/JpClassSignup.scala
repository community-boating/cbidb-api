package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.framework.Storable.FieldValues.{DateTimeFieldValue, IntFieldValue, StringFieldValue}
import com.coleji.framework.Storable.Fields.{DateTimeDatabaseField, IntDatabaseField, StringDatabaseField}
import com.coleji.framework.Storable._
import com.coleji.framework.Util.Initializable

class JpClassSignup extends StorableClass(JpClassSignup) {
	override object references extends ReferencesObject {
		val jpClassInstance = new Initializable[JpClassInstance]
		val jpClassWlResult = new Initializable[Option[JpClassWlResult]]
	}

	object values extends ValuesObject {
		val signupId = new IntFieldValue(self, JpClassSignup.fields.signupId)
		val instanceId = new IntFieldValue(self, JpClassSignup.fields.instanceId)
		val signupType = new StringFieldValue(self, JpClassSignup.fields.signupType)
		val personId = new IntFieldValue(self, JpClassSignup.fields.personId)
		val signupDatetime = new DateTimeFieldValue(self, JpClassSignup.fields.signupDatetime)
	}
}

object JpClassSignup extends StorableObject[JpClassSignup] {
	val entityName: String = "JP_CLASS_SIGNUPS"

	object fields extends FieldsObject {
		val signupId = new IntDatabaseField(self, "SIGNUP_ID")
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val signupType = new StringDatabaseField(self, "SIGNUP_TYPE", 1)
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val signupDatetime = new DateTimeDatabaseField(self, "SIGNUP_DATETIME")
	}

	def primaryKey: IntDatabaseField = fields.signupId
}