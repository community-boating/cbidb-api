package org.sailcbi.APIServer.Entities.JsFacades.Stripe

import com.coleji.framework.Core.{RequestCache, RequestCacheObject}
import com.coleji.framework.IO.PreparedQueries.PreparedValue
import com.coleji.framework.Storable.{CastableToStorableClass, CastableToStorableObject}
import org.sailcbi.APIServer.CbiUtil.GetSQLLiteral
import org.sailcbi.APIServer.UserTypes.{ApexRequestCache, MemberRequestCache, PublicRequestCache}
import play.api.libs.json.{JsValue, Json}

case class Charge(
	id: String,
	amount: Int,
	metadata: ChargeMetadata,
	created: Int,
	paid: Boolean,
	status: String,
	description: Option[String]
) extends CastableToStorableClass {
	val storableObject: CastableToStorableObject[_] = Charge
	val persistenceValues: Map[String, PreparedValue] = Charge.persistenceValues(this)
	val pkSqlLiteral: String = GetSQLLiteral(id)

	// e.g. re_abc%123%1000&re_def%456%2000  => refund # re_abc, close 123, refund 1000 cents; refund # re_def, close 456, refund 2000 cents
	def refunds: List[ChargeRefund] = {
		val outerSeparator = "&"
		val innerSeparator = "%"
		metadata.refunds match {
			case None => List.empty
			case Some(s) => s.split(outerSeparator)
					.toList
					.map(_.split(innerSeparator))
					.map(p => ChargeRefund(p(0), id, p(1).toInt, p(2).toInt))
		}
	}

	override def insertIntoLocalDB(rc: RequestCache): Unit = {
		rc.executePreparedQueryForInsert(this.getInsertPreparedQuery)
		this.refunds.foreach(r => rc.executePreparedQueryForInsert(r.getInsertPreparedQuery))
	}
}

object Charge extends StripeCastableToStorableObject[Charge] {
	implicit val chargeJSONFormat = Json.format[Charge]

	override val allowedUserTypes: Set[RequestCacheObject[_]] = Set(ApexRequestCache, MemberRequestCache, PublicRequestCache)

	def apply(v: JsValue): Charge = v.as[Charge]

	val apexTableName = "STRIPE_CHARGES"
	val persistenceFieldsMap: Map[String, Charge => PreparedValue] = Map(
		"CHARGE_ID" -> ((c: Charge) => c.id),
		"AMOUNT_IN_CENTS" -> ((c: Charge) => c.amount),
		"CREATED_EPOCH" -> ((c: Charge) => c.created),
		"PAID" -> ((c: Charge) => c.paid),
		"STATUS" -> ((c: Charge) => c.status),
		"CLOSE_ID" -> ((c: Charge) => c.metadata.closeId),
		"ORDER_ID" -> ((c: Charge) => c.metadata.orderId),
		"TOKEN" -> ((c: Charge) => c.metadata.token),
		"REFUNDS" -> ((c: Charge) => c.metadata.refunds),
		"DESCRIPTION" -> ((c: Charge) => c.description),
	)
	val pkColumnName = "CHARGE_ID"
	val getURL: String = "charges"
	val getId: Charge => String = _.id
}

/*
{
    "id": "ch_1BmZTqIvhpK9IRKVamkZAdVM",
    "object": "charge",
    "amount": 1234,
    "amount_refunded": 0,
    "application": null,
    "application_fee": null,
    "balance_transaction": "txn_1BmZTqIvhpK9IRKVNKtV9U7N",
    "captured": true,
    "created": 1516505210,
    "currency": "usd",
    "customer": null,
    "description": "Test1",
    "destination": null,
    "dispute": null,
    "failure_code": null,
    "failure_message": null,
    "fraud_details": {},
    "invoice": null,
    "livemode": false,
    "metadata": {},
    "on_behalf_of": null,
    "order": null,
    "outcome": {
        "network_status": "approved_by_network",
        "reason": null,
        "risk_level": "normal",
        "seller_message": "Payment complete.",
        "type": "authorized"
    },
    "paid": true,
    "receipt_email": null,
    "receipt_number": null,
    "refunded": false,
    "refunds": {
        "object": "list",
        "data": [],
        "has_more": false,
        "total_count": 0,
        "url": "/v1/charges/ch_1BmZTqIvhpK9IRKVamkZAdVM/refunds"
    },
    "review": null,
    "shipping": null,
    "source": {
        "id": "card_1BkktdIvhpK9IRKVNuXHXVTP",
        "object": "card",
        "address_city": null,
        "address_country": null,
        "address_line1": null,
        "address_line1_check": null,
        "address_line2": null,
        "address_state": null,
        "address_zip": "02138",
        "address_zip_check": "pass",
        "brand": "Visa",
        "country": "US",
        "customer": null,
        "cvc_check": null,
        "dynamic_last4": null,
        "exp_month": 11,
        "exp_year": 2021,
        "fingerprint": "gTe1Kzkic12Sst5a",
        "funding": "unknown",
        "last4": "1111",
        "metadata": {},
        "name": null,
        "tokenization_method": null
    },
    "source_transfer": null,
    "statement_descriptor": null,
    "status": "succeeded",
    "transfer_group": null
}
 */
