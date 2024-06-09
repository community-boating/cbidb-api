package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class YearlyDate extends StorableClass(YearlyDate) {
	override object values extends ValuesObject {
		val dateId = new IntFieldValue(self, YearlyDate.fields.dateId)
		val year = new IntFieldValue(self, YearlyDate.fields.year)
		val itemId = new IntFieldValue(self, YearlyDate.fields.itemId)
		val startDate = new DateTimeFieldValue(self, YearlyDate.fields.startDate)
		val createdOn = new NullableDateTimeFieldValue(self, YearlyDate.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, YearlyDate.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, YearlyDate.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, YearlyDate.fields.updatedBy)
		val endDate = new NullableDateTimeFieldValue(self, YearlyDate.fields.endDate)
	}
}

object YearlyDate extends StorableObject[YearlyDate] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "YEARLY_DATES"

	object fields extends FieldsObject {
		val dateId = new IntDatabaseField(self, "DATE_ID")
		@NullableInDatabase
		val year = new IntDatabaseField(self, "YEAR")
		@NullableInDatabase
		val itemId = new IntDatabaseField(self, "ITEM_ID")
		@NullableInDatabase
		val startDate = new DateTimeDatabaseField(self, "START_DATE")
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val endDate = new NullableDateTimeDatabaseField(self, "END_DATE")
	}

	def primaryKey: IntDatabaseField = fields.dateId
}