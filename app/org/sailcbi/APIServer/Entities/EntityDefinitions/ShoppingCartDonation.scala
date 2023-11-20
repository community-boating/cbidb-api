package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ShoppingCartDonation extends StorableClass(ShoppingCartDonation) {
	override object values extends ValuesObject {
		val itemId = new IntFieldValue(self, ShoppingCartDonation.fields.itemId)
		val amount = new DoubleFieldValue(self, ShoppingCartDonation.fields.amount)
		val createdOn = new DateTimeFieldValue(self, ShoppingCartDonation.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ShoppingCartDonation.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, ShoppingCartDonation.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ShoppingCartDonation.fields.updatedBy)
		val orderId = new IntFieldValue(self, ShoppingCartDonation.fields.orderId)
		val fundId = new IntFieldValue(self, ShoppingCartDonation.fields.fundId)
		val initiativeId = new IntFieldValue(self, ShoppingCartDonation.fields.initiativeId)
		val inMemoryOf = new NullableStringFieldValue(self, ShoppingCartDonation.fields.inMemoryOf)
		val isAnon = new NullableBooleanFieldValue(self, ShoppingCartDonation.fields.isAnon)
	}
}

object ShoppingCartDonation extends StorableObject[ShoppingCartDonation] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "SHOPPING_CART_DONATIONS"

	object fields extends FieldsObject {
		val itemId = new IntDatabaseField(self, "ITEM_ID")
		val amount = new DoubleDatabaseField(self, "AMOUNT")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val orderId = new IntDatabaseField(self, "ORDER_ID")
		val fundId = new IntDatabaseField(self, "FUND_ID")
		val initiativeId = new IntDatabaseField(self, "INITIATIVE_ID")
		val inMemoryOf = new NullableStringDatabaseField(self, "IN_MEMORY_OF", 500)
		val isAnon = new NullableBooleanDatabaseField(self, "IS_ANON")
	}

	def primaryKey: IntDatabaseField = fields.itemId
}