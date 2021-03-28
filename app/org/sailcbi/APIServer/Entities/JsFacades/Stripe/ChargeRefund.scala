package org.sailcbi.APIServer.Entities.JsFacades.Stripe

import com.coleji.framework.Core.RequestCacheObject
import com.coleji.framework.IO.PreparedQueries.PreparedValue
import com.coleji.framework.Storable.{CastableToStorableClass, CastableToStorableObject}
import org.sailcbi.APIServer.CbiUtil.GetSQLLiteral
import org.sailcbi.APIServer.UserTypes.ApexRequestCache
import play.api.libs.json.{JsValue, Json}

case class ChargeRefund(
							   refundId: String,
							   chargeId: String,
							   closeId: Int,
							   amountInCents: Int
					   ) extends CastableToStorableClass {
	val storableObject: CastableToStorableObject[_] = ChargeRefund
	val persistenceValues: Map[String, PreparedValue] = ChargeRefund.persistenceValues(this)
	val pkSqlLiteral: String = GetSQLLiteral(refundId)
}

object ChargeRefund extends StripeCastableToStorableObject[ChargeRefund] {
	implicit val chargeRefundJSONFormat = Json.format[ChargeRefund]

	override val allowedUserTypes: Set[RequestCacheObject[_]] = Set(ApexRequestCache)

	def apply(v: JsValue): ChargeRefund = v.as[ChargeRefund]

	val apexTableName = "STRIPE_REFUNDS"
	val persistenceFieldsMap: Map[String, ChargeRefund => PreparedValue] = Map(
		"REFUND_ID" -> ((r: ChargeRefund) => r.refundId),
		"CHARGE_ID" -> ((r: ChargeRefund) => r.chargeId),
		"CLOSE_ID" -> ((r: ChargeRefund) => r.closeId),
		"AMOUNT_IN_CENTS" -> ((r: ChargeRefund) => r.amountInCents)
	)
	val pkColumnName = "REFUND_ID"
	val getURL: String = "refunds"
	val getId: ChargeRefund => String = _.refundId
}