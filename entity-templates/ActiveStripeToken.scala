package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ActiveStripeToken extends StorableClass(ActiveStripeToken) {
	object values extends ValuesObject {
		val tokenId = new IntFieldValue(self, ActiveStripeToken.fields.tokenId)
		val orderId = new NullableIntFieldValue(self, ActiveStripeToken.fields.orderId)
		val token = new NullableStringFieldValue(self, ActiveStripeToken.fields.token)
		val createdDatetime = new NullableLocalDateTimeFieldValue(self, ActiveStripeToken.fields.createdDatetime)
		val cardLastDigits = new NullableStringFieldValue(self, ActiveStripeToken.fields.cardLastDigits)
		val cardExpMonth = new NullableDoubleFieldValue(self, ActiveStripeToken.fields.cardExpMonth)
		val cardExpYear = new NullableDoubleFieldValue(self, ActiveStripeToken.fields.cardExpYear)
		val cardZip = new NullableStringFieldValue(self, ActiveStripeToken.fields.cardZip)
	}
}

object ActiveStripeToken extends StorableObject[ActiveStripeToken] {
	val entityName: String = "ACTIVE_STRIPE_TOKENS"

	object fields extends FieldsObject {
		val tokenId = new IntDatabaseField(self, "TOKEN_ID")
		val orderId = new NullableIntDatabaseField(self, "ORDER_ID")
		val token = new NullableStringDatabaseField(self, "TOKEN", 50)
		val createdDatetime = new NullableLocalDateTimeDatabaseField(self, "CREATED_DATETIME")
		val cardLastDigits = new NullableStringDatabaseField(self, "CARD_LAST_DIGITS", 10)
		val cardExpMonth = new NullableDoubleDatabaseField(self, "CARD_EXP_MONTH")
		val cardExpYear = new NullableDoubleDatabaseField(self, "CARD_EXP_YEAR")
		val cardZip = new NullableStringDatabaseField(self, "CARD_ZIP", 20)
	}
}