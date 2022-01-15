package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class YearlyDate extends StorableClass(YearlyDate) {
	object values extends ValuesObject {
		val dateId = new IntFieldValue(self, YearlyDate.fields.dateId)
		val year = new NullableDoubleFieldValue(self, YearlyDate.fields.year)
		val itemId = new NullableIntFieldValue(self, YearlyDate.fields.itemId)
		val startDate = new NullableLocalDateTimeFieldValue(self, YearlyDate.fields.startDate)
		val createdOn = new NullableLocalDateTimeFieldValue(self, YearlyDate.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, YearlyDate.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, YearlyDate.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, YearlyDate.fields.updatedBy)
		val endDate = new NullableLocalDateTimeFieldValue(self, YearlyDate.fields.endDate)
	}
}

object YearlyDate extends StorableObject[YearlyDate] {
	val entityName: String = "YEARLY_DATES"

	object fields extends FieldsObject {
		val dateId = new IntDatabaseField(self, "DATE_ID")
		val year = new NullableDoubleDatabaseField(self, "YEAR")
		val itemId = new NullableIntDatabaseField(self, "ITEM_ID")
		val startDate = new NullableLocalDateTimeDatabaseField(self, "START_DATE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val endDate = new NullableLocalDateTimeDatabaseField(self, "END_DATE")
	}

	def primaryKey: IntDatabaseField = fields.dateId
}