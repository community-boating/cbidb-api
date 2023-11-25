package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class HighSchoolPayment extends StorableClass(HighSchoolPayment) {
	override object values extends ValuesObject {
		val paymentId = new IntFieldValue(self, HighSchoolPayment.fields.paymentId)
		val schoolId = new IntFieldValue(self, HighSchoolPayment.fields.schoolId)
		val amount = new DoubleFieldValue(self, HighSchoolPayment.fields.amount)
		val closeId = new IntFieldValue(self, HighSchoolPayment.fields.closeId)
		val voidCloseId = new NullableIntFieldValue(self, HighSchoolPayment.fields.voidCloseId)
		val createdOn = new NullableDateTimeFieldValue(self, HighSchoolPayment.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, HighSchoolPayment.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, HighSchoolPayment.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, HighSchoolPayment.fields.updatedBy)
		val paymentMedium = new NullableStringFieldValue(self, HighSchoolPayment.fields.paymentMedium)
		val ccTransNum = new NullableDoubleFieldValue(self, HighSchoolPayment.fields.ccTransNum)
		val checkId = new IntFieldValue(self, HighSchoolPayment.fields.checkId)
		val springFall = new StringFieldValue(self, HighSchoolPayment.fields.springFall)
		val year = new IntFieldValue(self, HighSchoolPayment.fields.year)
	}
}

object HighSchoolPayment extends StorableObject[HighSchoolPayment] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "HIGH_SCHOOL_PAYMENTS"

	object fields extends FieldsObject {
		val paymentId = new IntDatabaseField(self, "PAYMENT_ID")
		val schoolId = new IntDatabaseField(self, "SCHOOL_ID")
		val amount = new DoubleDatabaseField(self, "AMOUNT")
		val closeId = new IntDatabaseField(self, "CLOSE_ID")
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 50)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 50)
		val paymentMedium = new NullableStringDatabaseField(self, "PAYMENT_MEDIUM", 50)
		val ccTransNum = new NullableDoubleDatabaseField(self, "CC_TRANS_NUM")
		@NullableInDatabase
		val checkId = new IntDatabaseField(self, "CHECK_ID")
		val springFall = new StringDatabaseField(self, "SPRING_FALL", 1)
		val year = new IntDatabaseField(self, "YEAR")
	}

	def primaryKey: IntDatabaseField = fields.paymentId
}