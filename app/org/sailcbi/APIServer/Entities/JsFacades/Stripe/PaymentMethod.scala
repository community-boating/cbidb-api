package org.sailcbi.APIServer.Entities.JsFacades.Stripe

import org.sailcbi.APIServer.CbiUtil.GetSQLLiteral
import org.sailcbi.APIServer.Entities.{CastableToStorableClass, CastableToStorableObject}
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedValue
import org.sailcbi.APIServer.Services.Authentication.{ApexRequestCache, MemberRequestCache}
import org.sailcbi.APIServer.Services.RequestCacheObject
import play.api.libs.json.{JsValue, Json}

case class PaymentMethod(
	id: String,
	customer: String,
	billing_details: PaymentMethodBillingDetails,
	card: PaymentMethodCard
)extends CastableToStorableClass {
	val storableObject: CastableToStorableObject[_] = PaymentMethod
	val persistenceValues: Map[String, PreparedValue] = PaymentMethod.persistenceValues(this)
	val pkSqlLiteral: String = GetSQLLiteral(id)
}

object PaymentMethod extends StripeCastableToStorableObject[PaymentMethod] {
	implicit val cardFormat = PaymentMethodCard.format
	implicit val addressFormat = PaymentMethodBillingDetailsAddress.format
	implicit val billingDetailsFormat = PaymentMethodBillingDetails.format
	implicit val paymentMethodFormat = Json.format[PaymentMethod]
	def apply(v: JsValue): PaymentMethod = v.as[PaymentMethod]

	override val allowedUserTypes: Set[RequestCacheObject[_]] = Set(ApexRequestCache, MemberRequestCache)

	val apexTableName = "STRIPE_PAYMENT_METHODS"
	val persistenceFieldsMap: Map[String, PaymentMethod => PreparedValue] = Map(
		"PAYMENT_METHOD_ID" -> ((pm: PaymentMethod) => pm.id),
		"CUSTOMER_ID" -> ((pm: PaymentMethod) => pm.customer),
		"CARD_ZIP" -> ((pm: PaymentMethod) => pm.billing_details.address.postal_code),
		"CARD_LAST_DIGITS" -> ((pm: PaymentMethod) => pm.card.last4),
		"CARD_EXP_MONTH" -> ((pm: PaymentMethod) => pm.card.exp_month),
		"CARD_EXP_YEAR" -> ((pm: PaymentMethod) => pm.card.exp_year),
	)
	val pkColumnName = "PAYMENT_METHOD_ID"
	val getURL: String = "payment_methods"
	val getId: PaymentMethod => String = _.id
}


case class PaymentMethodBillingDetails (
	address: PaymentMethodBillingDetailsAddress
)
object PaymentMethodBillingDetails {
	implicit val format = Json.format[PaymentMethodBillingDetails]
	def apply(v: JsValue): PaymentMethodBillingDetails = v.as[PaymentMethodBillingDetails]
}



case class PaymentMethodBillingDetailsAddress (
	postal_code: Option[String]
)
object PaymentMethodBillingDetailsAddress {
	implicit val format = Json.format[PaymentMethodBillingDetailsAddress]
	def apply(v: JsValue): PaymentMethodBillingDetailsAddress = v.as[PaymentMethodBillingDetailsAddress]
}




case class PaymentMethodCard (
	exp_month: Int,
	exp_year: Int,
	last4: String
)
object PaymentMethodCard {
	implicit val format = Json.format[PaymentMethodCard]
	def apply(v: JsValue): PaymentMethodCard = v.as[PaymentMethodCard]
}