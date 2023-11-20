package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class DiscountCode extends StorableClass(DiscountCode) {
	override object values extends ValuesObject {
		val codeId = new IntFieldValue(self, DiscountCode.fields.codeId)
		val email = new StringFieldValue(self, DiscountCode.fields.email)
		val discountId = new IntFieldValue(self, DiscountCode.fields.discountId)
		val createdOn = new NullableDateTimeFieldValue(self, DiscountCode.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, DiscountCode.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, DiscountCode.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, DiscountCode.fields.updatedBy)
		val code = new StringFieldValue(self, DiscountCode.fields.code)
		val name = new StringFieldValue(self, DiscountCode.fields.name)
	}
}

object DiscountCode extends StorableObject[DiscountCode] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "DISCOUNT_CODES"

	object fields extends FieldsObject {
		val codeId = new IntDatabaseField(self, "CODE_ID")
		val email = new StringDatabaseField(self, "EMAIL", 500)
		val discountId = new IntDatabaseField(self, "DISCOUNT_ID")
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val code = new StringDatabaseField(self, "CODE", 50)
		val name = new StringDatabaseField(self, "NAME", 500)
	}

	def primaryKey: IntDatabaseField = fields.codeId
}