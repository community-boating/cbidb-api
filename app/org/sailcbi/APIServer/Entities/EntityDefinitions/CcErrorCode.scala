package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class CcErrorCode extends StorableClass(CcErrorCode) {
	override object values extends ValuesObject {
		val errorId = new IntFieldValue(self, CcErrorCode.fields.errorId)
		val errorCode = new NullableStringFieldValue(self, CcErrorCode.fields.errorCode)
		val displayErrorMessage = new NullableStringFieldValue(self, CcErrorCode.fields.displayErrorMessage)
		val createdOn = new NullableDateTimeFieldValue(self, CcErrorCode.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, CcErrorCode.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, CcErrorCode.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, CcErrorCode.fields.updatedBy)
		val sageErrorMessage = new NullableStringFieldValue(self, CcErrorCode.fields.sageErrorMessage)
		val errorTitle = new NullableStringFieldValue(self, CcErrorCode.fields.errorTitle)
	}
}

object CcErrorCode extends StorableObject[CcErrorCode] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "CC_ERROR_CODES"

	object fields extends FieldsObject {
		val errorId = new IntDatabaseField(self, "ERROR_ID")
		val errorCode = new NullableStringDatabaseField(self, "ERROR_CODE", 10)
		val displayErrorMessage = new NullableStringDatabaseField(self, "DISPLAY_ERROR_MESSAGE", 4000)
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val sageErrorMessage = new NullableStringDatabaseField(self, "SAGE_ERROR_MESSAGE", 4000)
		val errorTitle = new NullableStringDatabaseField(self, "ERROR_TITLE", 200)
	}

	def primaryKey: IntDatabaseField = fields.errorId
}