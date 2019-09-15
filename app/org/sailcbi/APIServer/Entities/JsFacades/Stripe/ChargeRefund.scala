package org.sailcbi.APIServer.Entities.JsFacades.Stripe

import org.sailcbi.APIServer.CbiUtil.{GetSQLLiteral, GetSQLLiteralPrepared}
import org.sailcbi.APIServer.Entities.{CastableToStorableClass, CastableToStorableObject}
import play.api.libs.json.{JsValue, Json}

case class ChargeRefund(
							   refundId: String,
							   chargeId: String,
							   closeId: Int,
							   amountInCents: Int
					   ) extends CastableToStorableClass {
	val storableObject: CastableToStorableObject[_] = ChargeRefund
	val persistenceValues: Map[String, String] = ChargeRefund.persistenceValues(this)
	val pkSqlLiteral: String = GetSQLLiteral(refundId)
}

object ChargeRefund extends StripeCastableToStorableObject[ChargeRefund] {
	implicit val chargeRefundJSONFormat = Json.format[ChargeRefund]

	def apply(v: JsValue): ChargeRefund = v.as[ChargeRefund]

	val apexTableName = "STRIPE_REFUNDS"
	val persistenceFieldsMap: Map[String, ChargeRefund => String] = Map(
		"REFUND_ID" -> ((r: ChargeRefund) => GetSQLLiteralPrepared(r.refundId)),
		"CHARGE_ID" -> ((r: ChargeRefund) => GetSQLLiteralPrepared(r.chargeId)),
		"CLOSE_ID" -> ((r: ChargeRefund) => GetSQLLiteralPrepared(r.closeId)),
		"AMOUNT_IN_CENTS" -> ((r: ChargeRefund) => GetSQLLiteralPrepared(r.amountInCents))
	)
	val pkColumnName = "REFUND_ID"
	val getURL: String = "refunds"
	val getId: ChargeRefund => String = _.refundId
}