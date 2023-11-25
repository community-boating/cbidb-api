package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class HighSchoolFee extends StorableClass(HighSchoolFee) {
	override object values extends ValuesObject {
		val rowId = new IntFieldValue(self, HighSchoolFee.fields.rowId)
		val schoolId = new IntFieldValue(self, HighSchoolFee.fields.schoolId)
		val year = new IntFieldValue(self, HighSchoolFee.fields.year)
		val springFall = new StringFieldValue(self, HighSchoolFee.fields.springFall)
		val amount = new DoubleFieldValue(self, HighSchoolFee.fields.amount)
		val closeId = new IntFieldValue(self, HighSchoolFee.fields.closeId)
		val voidCloseId = new NullableIntFieldValue(self, HighSchoolFee.fields.voidCloseId)
		val createdOn = new NullableDateTimeFieldValue(self, HighSchoolFee.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, HighSchoolFee.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, HighSchoolFee.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, HighSchoolFee.fields.updatedBy)
	}
}

object HighSchoolFee extends StorableObject[HighSchoolFee] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "HIGH_SCHOOL_FEES"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val schoolId = new IntDatabaseField(self, "SCHOOL_ID")
		val year = new IntDatabaseField(self, "YEAR")
		val springFall = new StringDatabaseField(self, "SPRING_FALL", 1)
		@NullableInDatabase
		val amount = new DoubleDatabaseField(self, "AMOUNT")
		@NullableInDatabase
		val closeId = new IntDatabaseField(self, "CLOSE_ID")
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 50)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 50)
	}

	def primaryKey: IntDatabaseField = fields.rowId
}