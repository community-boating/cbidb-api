package org.sailcbi.APIServer.IO.PreparedQueries.Apex

import org.sailcbi.APIServer.CbiUtil.DateUtil
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.{Charge, ChargeMetadata, Payout}
import org.sailcbi.APIServer.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.ApexUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, ResultSetWrapper}

class GetLocalStripeCharges (implicit PA: PermissionsAuthority) extends HardcodedQueryForSelect[Charge](Set(ApexUserType), true) {
	val getQuery: String =
		s"""
		   |select CHARGE_ID, AMOUNT_IN_CENTS, CLOSE_ID, ORDER_ID, token, CREATED_EPOCH, PAID, status, refunds, description
		   |from STRIPE_CHARGES
		   |order by created_datetime
		   |
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Charge = {
		val cmd = ChargeMetadata(
			closeId = rs.getOptionInt(3).map(_.toString), //Some(rs.getInt(3).toString),
			orderId = rs.getOptionInt(4).map(_.toString), //Some(rs.getInt(4).toString),
			token = rs.getOptionString(5), // Some(rs.getString(5)),
			cbiInstance = Some(PA.instanceName),
			refunds = rs.getOptionString(9)
		)

		Charge(
			id = rs.getString(1),
			amount = rs.getInt(2),
			metadata = cmd,
			created = rs.getInt(6),
			paid = rs.getString(7) == "Y",
			status = rs.getString(8),
			description = rs.getOptionString(10)
		)
	}
}