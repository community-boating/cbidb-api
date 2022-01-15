package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class CloseSummary extends StorableClass(CloseSummary) {
	object values extends ValuesObject {
		val showInSales = new NullableBooleanFIeldValue(self, CloseSummary.fields.showInSales)
		val link = new NullableStringFieldValue(self, CloseSummary.fields.link)
		val isRetail = new NullableStringFieldValue(self, CloseSummary.fields.isRetail)
		val closeId = new NullableIntFieldValue(self, CloseSummary.fields.closeId)
		val location = new NullableStringFieldValue(self, CloseSummary.fields.location)
		val item = new NullableStringFieldValue(self, CloseSummary.fields.item)
		val discount = new NullableStringFieldValue(self, CloseSummary.fields.discount)
		val count = new NullableDoubleFieldValue(self, CloseSummary.fields.count)
		val totalPrice = new NullableDoubleFieldValue(self, CloseSummary.fields.totalPrice)
		val totalPretax = new NullableDoubleFieldValue(self, CloseSummary.fields.totalPretax)
		val taxAmount = new NullableDoubleFieldValue(self, CloseSummary.fields.taxAmount)
	}
}

object CloseSummary extends StorableObject[CloseSummary] {
	val entityName: String = "CLOSE_SUMMARY"

	object fields extends FieldsObject {
		val showInSales = new NullableBooleanDatabaseField(self, "SHOW_IN_SALES")
		val link = new NullableStringDatabaseField(self, "LINK", 217)
		val isRetail = new NullableStringDatabaseField(self, "IS_RETAIL", 1)
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val location = new NullableStringDatabaseField(self, "LOCATION", 9)
		val item = new NullableStringDatabaseField(self, "ITEM", 220)
		val discount = new NullableStringDatabaseField(self, "DISCOUNT", 107)
		val count = new NullableDoubleDatabaseField(self, "COUNT")
		val totalPrice = new NullableDoubleDatabaseField(self, "TOTAL_PRICE")
		val totalPretax = new NullableDoubleDatabaseField(self, "TOTAL_PRETAX")
		val taxAmount = new NullableDoubleDatabaseField(self, "TAX_AMOUNT")
	}
}