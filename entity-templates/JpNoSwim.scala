package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpNoSwim extends StorableClass(JpNoSwim) {
	object values extends ValuesObject {
		val swimId = new IntFieldValue(self, JpNoSwim.fields.swimId)
		val cardNum = new NullableStringFieldValue(self, JpNoSwim.fields.cardNum)
		val createdOn = new NullableLocalDateTimeFieldValue(self, JpNoSwim.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpNoSwim.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, JpNoSwim.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpNoSwim.fields.updatedBy)
	}
}

object JpNoSwim extends StorableObject[JpNoSwim] {
	val entityName: String = "JP_NO_SWIM"

	object fields extends FieldsObject {
		val swimId = new IntDatabaseField(self, "SWIM_ID")
		val cardNum = new NullableStringDatabaseField(self, "CARD_NUM", 20)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.swimId
}