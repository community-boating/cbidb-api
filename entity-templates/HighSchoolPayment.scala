package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class HighSchoolPayment extends StorableClass(HighSchoolPayment) {
	object values extends ValuesObject {
		val paymentId = new IntFieldValue(self, HighSchoolPayment.fields.paymentId)
		val schoolId = new IntFieldValue(self, HighSchoolPayment.fields.schoolId)
		val amount = new DoubleFieldValue(self, HighSchoolPayment.fields.amount)
		val closeId = new IntFieldValue(self, HighSchoolPayment.fields.closeId)
		val voidCloseId = new NullableIntFieldValue(self, HighSchoolPayment.fields.voidCloseId)
		val createdOn = new NullableLocalDateTimeFieldValue(self, HighSchoolPayment.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, HighSchoolPayment.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, HighSchoolPayment.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, HighSchoolPayment.fields.updatedBy)
		val paymentMedium = new NullableStringFieldValue(self, HighSchoolPayment.fields.paymentMedium)
		val ccTransNum = new NullableDoubleFieldValue(self, HighSchoolPayment.fields.ccTransNum)
		val year = new DoubleFieldValue(self, HighSchoolPayment.fields.year)
		val springFall = new BooleanFIeldValue(self, HighSchoolPayment.fields.springFall)
		val checkId = new NullableIntFieldValue(self, HighSchoolPayment.fields.checkId)
	}
}

object HighSchoolPayment extends StorableObject[HighSchoolPayment] {
	val entityName: String = "HIGH_SCHOOL_PAYMENTS"

	object fields extends FieldsObject {
		val paymentId = new IntDatabaseField(self, "PAYMENT_ID")
		val schoolId = new IntDatabaseField(self, "SCHOOL_ID")
		val amount = new DoubleDatabaseField(self, "AMOUNT")
		val closeId = new IntDatabaseField(self, "CLOSE_ID")
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 50)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 50)
		val paymentMedium = new NullableStringDatabaseField(self, "PAYMENT_MEDIUM", 50)
		val ccTransNum = new NullableDoubleDatabaseField(self, "CC_TRANS_NUM")
		val year = new DoubleDatabaseField(self, "YEAR")
		val springFall = new BooleanDatabaseField(self, "SPRING_FALL")
		val checkId = new NullableIntDatabaseField(self, "CHECK_ID")
	}

	def primaryKey: IntDatabaseField = fields.paymentId
}