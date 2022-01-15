package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class CcLog extends StorableClass(CcLog) {
	object values extends ValuesObject {
		val logId = new IntFieldValue(self, CcLog.fields.logId)
		val logDatetime = new NullableLocalDateTimeFieldValue(self, CcLog.fields.logDatetime)
		val cName = new NullableStringFieldValue(self, CcLog.fields.cName)
		val cAddr = new NullableStringFieldValue(self, CcLog.fields.cAddr)
		val cCity = new NullableStringFieldValue(self, CcLog.fields.cCity)
		val cState = new NullableStringFieldValue(self, CcLog.fields.cState)
		val cZip = new NullableStringFieldValue(self, CcLog.fields.cZip)
		val cCountry = new NullableStringFieldValue(self, CcLog.fields.cCountry)
		val cEmail = new NullableStringFieldValue(self, CcLog.fields.cEmail)
		val cCardnumber = new NullableStringFieldValue(self, CcLog.fields.cCardnumber)
		val cExp = new NullableStringFieldValue(self, CcLog.fields.cExp)
		val tAmt = new NullableDoubleFieldValue(self, CcLog.fields.tAmt)
		val tCode = new NullableStringFieldValue(self, CcLog.fields.tCode)
		val tOrdernum = new NullableStringFieldValue(self, CcLog.fields.tOrdernum)
		val tAuth = new NullableStringFieldValue(self, CcLog.fields.tAuth)
		val tReference = new NullableStringFieldValue(self, CcLog.fields.tReference)
		val response = new NullableStringFieldValue(self, CcLog.fields.response)
		val rApprov = new NullableBooleanFIeldValue(self, CcLog.fields.rApprov)
		val rCode = new NullableStringFieldValue(self, CcLog.fields.rCode)
		val rMessage = new NullableStringFieldValue(self, CcLog.fields.rMessage)
		val rCvv = new NullableBooleanFIeldValue(self, CcLog.fields.rCvv)
		val rAvs = new NullableBooleanFIeldValue(self, CcLog.fields.rAvs)
		val rRisk = new NullableStringFieldValue(self, CcLog.fields.rRisk)
		val rReference = new NullableStringFieldValue(self, CcLog.fields.rReference)
		val rOrdernumber = new NullableStringFieldValue(self, CcLog.fields.rOrdernumber)
		val createdOn = new NullableLocalDateTimeFieldValue(self, CcLog.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, CcLog.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, CcLog.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, CcLog.fields.updatedBy)
	}
}

object CcLog extends StorableObject[CcLog] {
	val entityName: String = "CC_LOG"

	object fields extends FieldsObject {
		val logId = new IntDatabaseField(self, "LOG_ID")
		val logDatetime = new NullableLocalDateTimeDatabaseField(self, "LOG_DATETIME")
		val cName = new NullableStringDatabaseField(self, "C_NAME", 500)
		val cAddr = new NullableStringDatabaseField(self, "C_ADDR", 500)
		val cCity = new NullableStringDatabaseField(self, "C_CITY", 50)
		val cState = new NullableStringDatabaseField(self, "C_STATE", 50)
		val cZip = new NullableStringDatabaseField(self, "C_ZIP", 10)
		val cCountry = new NullableStringDatabaseField(self, "C_COUNTRY", 100)
		val cEmail = new NullableStringDatabaseField(self, "C_EMAIL", 100)
		val cCardnumber = new NullableStringDatabaseField(self, "C_CARDNUMBER", 30)
		val cExp = new NullableStringDatabaseField(self, "C_EXP", 10)
		val tAmt = new NullableDoubleDatabaseField(self, "T_AMT")
		val tCode = new NullableStringDatabaseField(self, "T_CODE", 5)
		val tOrdernum = new NullableStringDatabaseField(self, "T_ORDERNUM", 20)
		val tAuth = new NullableStringDatabaseField(self, "T_AUTH", 50)
		val tReference = new NullableStringDatabaseField(self, "T_REFERENCE", 50)
		val response = new NullableStringDatabaseField(self, "RESPONSE", 200)
		val rApprov = new NullableBooleanDatabaseField(self, "R_APPROV")
		val rCode = new NullableStringDatabaseField(self, "R_CODE", 10)
		val rMessage = new NullableStringDatabaseField(self, "R_MESSAGE", 50)
		val rCvv = new NullableBooleanDatabaseField(self, "R_CVV")
		val rAvs = new NullableBooleanDatabaseField(self, "R_AVS")
		val rRisk = new NullableStringDatabaseField(self, "R_RISK", 50)
		val rReference = new NullableStringDatabaseField(self, "R_REFERENCE", 100)
		val rOrdernumber = new NullableStringDatabaseField(self, "R_ORDERNUMBER", 100)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.logId
}