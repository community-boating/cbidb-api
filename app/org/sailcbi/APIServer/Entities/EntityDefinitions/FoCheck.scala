package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class FoCheck extends StorableClass(FoCheck) {
	override object values extends ValuesObject {
		val checkId = new IntFieldValue(self, FoCheck.fields.checkId)
		val closeId = new IntFieldValue(self, FoCheck.fields.closeId)
		val value = new DoubleFieldValue(self, FoCheck.fields.value)
		val checkNum = new NullableStringFieldValue(self, FoCheck.fields.checkNum)
		val createdOn = new DateTimeFieldValue(self, FoCheck.fields.createdOn)
		val createdBy = new StringFieldValue(self, FoCheck.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, FoCheck.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, FoCheck.fields.updatedBy)
		val checkName = new NullableStringFieldValue(self, FoCheck.fields.checkName)
		val voidCloseId = new NullableIntFieldValue(self, FoCheck.fields.voidCloseId)
		val checkDate = new NullableDateTimeFieldValue(self, FoCheck.fields.checkDate)
	}
}

object FoCheck extends StorableObject[FoCheck] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "FO_CHECKS"

	object fields extends FieldsObject {
		val checkId = new IntDatabaseField(self, "CHECK_ID")
		val closeId = new IntDatabaseField(self, "CLOSE_ID")
		val value = new DoubleDatabaseField(self, "VALUE")
		val checkNum = new NullableStringDatabaseField(self, "CHECK_NUM", 50)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val checkName = new NullableStringDatabaseField(self, "CHECK_NAME", 500)
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
		val checkDate = new NullableDateTimeDatabaseField(self, "CHECK_DATE")
	}

	def primaryKey: IntDatabaseField = fields.checkId
}