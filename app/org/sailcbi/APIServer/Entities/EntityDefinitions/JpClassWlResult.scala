package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.framework.Storable.FieldValues.{IntFieldValue, StringFieldValue}
import com.coleji.framework.Storable.Fields.{IntDatabaseField, StringDatabaseField}
import com.coleji.framework.Storable._
import com.coleji.framework.Util.Initializable

class JpClassWlResult extends StorableClass {
	this.setCompanion(JpClassWlResult)

	object references extends ReferencesObject {
		var jpClassSignup = new Initializable[JpClassSignup]
	}

	object values extends ValuesObject {
		val signupId = new IntFieldValue(self, JpClassWlResult.fields.signupId)
		val wlResult = new StringFieldValue(self, JpClassWlResult.fields.wlResult)
	}

	override val valuesList = List(
		values.signupId,
		values.wlResult
	)
}

object JpClassWlResult extends StorableObject[JpClassWlResult] {
	val entityName: String = "JP_CLASS_WL_RESULTS"

	object fields extends FieldsObject {
		val signupId = new IntDatabaseField(self, "SIGNUP_ID")
		val wlResult = new StringDatabaseField(self, "WL_RESULT", 1)
	}

	def primaryKey: IntDatabaseField = fields.signupId
}