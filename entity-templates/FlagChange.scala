package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FlagChange extends StorableClass(FlagChange) {
	object values extends ValuesObject {
		val changeId = new IntFieldValue(self, FlagChange.fields.changeId)
		val flag = new BooleanFIeldValue(self, FlagChange.fields.flag)
		val changeDatetime = new LocalDateTimeFieldValue(self, FlagChange.fields.changeDatetime)
		val createdOn = new NullableLocalDateTimeFieldValue(self, FlagChange.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, FlagChange.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, FlagChange.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, FlagChange.fields.updatedBy)
	}
}

object FlagChange extends StorableObject[FlagChange] {
	val entityName: String = "FLAG_CHANGES"

	object fields extends FieldsObject {
		val changeId = new IntDatabaseField(self, "CHANGE_ID")
		val flag = new BooleanDatabaseField(self, "FLAG")
		val changeDatetime = new LocalDateTimeDatabaseField(self, "CHANGE_DATETIME")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.changeId
}