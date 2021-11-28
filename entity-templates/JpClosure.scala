package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpClosure extends StorableClass(JpClosure) {
	object values extends ValuesObject {
		val closureId = new IntFieldValue(self, JpClosure.fields.closureId)
		val drDate = new NullableLocalDateTimeFieldValue(self, JpClosure.fields.drDate)
		val closeTime = new NullableLocalDateTimeFieldValue(self, JpClosure.fields.closeTime)
		val reopenTime = new NullableLocalDateTimeFieldValue(self, JpClosure.fields.reopenTime)
		val createdOn = new NullableLocalDateTimeFieldValue(self, JpClosure.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpClosure.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, JpClosure.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpClosure.fields.updatedBy)
	}
}

object JpClosure extends StorableObject[JpClosure] {
	val entityName: String = "JP_CLOSURES"

	object fields extends FieldsObject {
		val closureId = new IntDatabaseField(self, "CLOSURE_ID")
		val drDate = new NullableLocalDateTimeDatabaseField(self, "DR_DATE")
		val closeTime = new NullableLocalDateTimeDatabaseField(self, "CLOSE_TIME")
		val reopenTime = new NullableLocalDateTimeDatabaseField(self, "REOPEN_TIME")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.closureId
}