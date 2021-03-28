package org.sailcbi.APIServer.IO.PreparedQueries.Apex

import com.coleji.framework.Core.PermissionsAuthority
import com.coleji.framework.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.{Charge, ChargeMetadata}
import org.sailcbi.APIServer.Services.ResultSetWrapper
import org.sailcbi.APIServer.UserTypes.ApexRequestCache

class GetLocalStripeCharges (implicit PA: PermissionsAuthority) extends HardcodedQueryForSelect[Charge](Set(ApexRequestCache), true) {
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