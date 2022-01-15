package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class HighSchoolFee extends StorableClass(HighSchoolFee) {
	object values extends ValuesObject {
		val rowId = new IntFieldValue(self, HighSchoolFee.fields.rowId)
		val schoolId = new IntFieldValue(self, HighSchoolFee.fields.schoolId)
		val year = new DoubleFieldValue(self, HighSchoolFee.fields.year)
		val springFall = new BooleanFIeldValue(self, HighSchoolFee.fields.springFall)
		val amount = new NullableDoubleFieldValue(self, HighSchoolFee.fields.amount)
		val closeId = new NullableIntFieldValue(self, HighSchoolFee.fields.closeId)
		val voidCloseId = new NullableIntFieldValue(self, HighSchoolFee.fields.voidCloseId)
		val createdOn = new NullableLocalDateTimeFieldValue(self, HighSchoolFee.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, HighSchoolFee.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, HighSchoolFee.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, HighSchoolFee.fields.updatedBy)
	}
}

object HighSchoolFee extends StorableObject[HighSchoolFee] {
	val entityName: String = "HIGH_SCHOOL_FEES"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val schoolId = new IntDatabaseField(self, "SCHOOL_ID")
		val year = new DoubleDatabaseField(self, "YEAR")
		val springFall = new BooleanDatabaseField(self, "SPRING_FALL")
		val amount = new NullableDoubleDatabaseField(self, "AMOUNT")
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 50)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 50)
	}

	def primaryKey: IntDatabaseField = fields.rowId
}