package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.framework.Storable.FieldValues.{DateTimeFieldValue, IntFieldValue, NullableIntFieldValue, StringFieldValue}
import com.coleji.framework.Storable.Fields.{DateTimeDatabaseField, IntDatabaseField, NullableIntDatabaseField, StringDatabaseField}
import com.coleji.framework.Storable._
import com.coleji.framework.Util.Initializable
import org.sailcbi.APIServer.Entities.NullableInDatabase

class JpClassSignup extends StorableClass(JpClassSignup) {
	override object references extends ReferencesObject {
		val jpClassInstance = new Initializable[JpClassInstance]
		val jpClassWlResult = new Initializable[Option[JpClassWlResult]]
		val person = new Initializable[Person]
		val group = new Initializable[Option[JpGroup]]
		val section = new Initializable[Option[JpClassSection]]
	}

	object values extends ValuesObject {
		val signupId = new IntFieldValue(self, JpClassSignup.fields.signupId)
		val instanceId = new IntFieldValue(self, JpClassSignup.fields.instanceId)
		val signupType = new StringFieldValue(self, JpClassSignup.fields.signupType)
		val personId = new IntFieldValue(self, JpClassSignup.fields.personId)
		val signupDatetime = new DateTimeFieldValue(self, JpClassSignup.fields.signupDatetime)
		val sequence = new IntFieldValue(self, JpClassSignup.fields.sequence)
		val groupId = new NullableIntFieldValue(self, JpClassSignup.fields.groupId)
		val sectionid = new NullableIntFieldValue(self, JpClassSignup.fields.sectionId)
	}
}

object JpClassSignup extends StorableObject[JpClassSignup] {
	val entityName: String = "JP_CLASS_SIGNUPS"

	object fields extends FieldsObject {
		val signupId = new IntDatabaseField(self, "SIGNUP_ID")
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		@NullableInDatabase
		val signupType = new StringDatabaseField(self, "SIGNUP_TYPE", 1)
		val personId = new IntDatabaseField(self, "PERSON_ID")
		@NullableInDatabase
		val signupDatetime = new DateTimeDatabaseField(self, "SIGNUP_DATETIME")
		@NullableInDatabase
		val sequence = new IntDatabaseField(self, "SEQUENCE")
		val groupId = new NullableIntDatabaseField(self, "GROUP_ID")
		val sectionId = new NullableIntDatabaseField(self, "SECTION_ID")
	}

	def primaryKey: IntDatabaseField = fields.signupId
}