package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class StripeToken extends StorableClass(StripeToken) {
	override object values extends ValuesObject {
		val tokenId = new IntFieldValue(self, StripeToken.fields.tokenId)
		val token = new StringFieldValue(self, StripeToken.fields.token)
		val orderId = new NullableIntFieldValue(self, StripeToken.fields.orderId)
		val createdDatetime = new DateTimeFieldValue(self, StripeToken.fields.createdDatetime)
		val cardLastDigits = new NullableStringFieldValue(self, StripeToken.fields.cardLastDigits)
		val cardExpMonth = new NullableDoubleFieldValue(self, StripeToken.fields.cardExpMonth)
		val cardExpYear = new NullableDoubleFieldValue(self, StripeToken.fields.cardExpYear)
		val cardZip = new NullableStringFieldValue(self, StripeToken.fields.cardZip)
		val active = new BooleanFieldValue(self, StripeToken.fields.active)
	}
}

object StripeToken extends StorableObject[StripeToken] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "STRIPE_TOKENS"

	object fields extends FieldsObject {
		val tokenId = new IntDatabaseField(self, "TOKEN_ID")
		@NullableInDatabase
		val token = new StringDatabaseField(self, "TOKEN", 50)
		val orderId = new NullableIntDatabaseField(self, "ORDER_ID")
		@NullableInDatabase
		val createdDatetime = new DateTimeDatabaseField(self, "CREATED_DATETIME")
		val cardLastDigits = new NullableStringDatabaseField(self, "CARD_LAST_DIGITS", 10)
		val cardExpMonth = new NullableDoubleDatabaseField(self, "CARD_EXP_MONTH")
		val cardExpYear = new NullableDoubleDatabaseField(self, "CARD_EXP_YEAR")
		val cardZip = new NullableStringDatabaseField(self, "CARD_ZIP", 20)
		val active = new BooleanDatabaseField(self, "ACTIVE", false)
	}

	def primaryKey: IntDatabaseField = fields.tokenId
}