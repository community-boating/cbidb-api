package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FoCheck extends StorableClass(FoCheck) {
	object values extends ValuesObject {
		val checkId = new IntFieldValue(self, FoCheck.fields.checkId)
		val closeId = new IntFieldValue(self, FoCheck.fields.closeId)
		val value = new DoubleFieldValue(self, FoCheck.fields.value)
		val checkNum = new NullableStringFieldValue(self, FoCheck.fields.checkNum)
		val createdOn = new NullableLocalDateTimeFieldValue(self, FoCheck.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, FoCheck.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, FoCheck.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, FoCheck.fields.updatedBy)
		val checkName = new NullableStringFieldValue(self, FoCheck.fields.checkName)
		val voidCloseId = new NullableIntFieldValue(self, FoCheck.fields.voidCloseId)
		val checkDate = new NullableLocalDateTimeFieldValue(self, FoCheck.fields.checkDate)
	}
}

object FoCheck extends StorableObject[FoCheck] {
	val entityName: String = "FO_CHECKS"

	object fields extends FieldsObject {
		val checkId = new IntDatabaseField(self, "CHECK_ID")
		val closeId = new IntDatabaseField(self, "CLOSE_ID")
		val value = new DoubleDatabaseField(self, "VALUE")
		val checkNum = new NullableStringDatabaseField(self, "CHECK_NUM", 50)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val checkName = new NullableStringDatabaseField(self, "CHECK_NAME", 500)
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
		val checkDate = new NullableLocalDateTimeDatabaseField(self, "CHECK_DATE")
	}

	def primaryKey: IntDatabaseField = fields.checkId
}