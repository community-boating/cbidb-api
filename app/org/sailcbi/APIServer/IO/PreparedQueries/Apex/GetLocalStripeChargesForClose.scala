package org.sailcbi.APIServer.IO.PreparedQueries.Apex

import java.sql.ResultSet

import org.sailcbi.APIServer.Entities.JsFacades.Stripe.{Charge, ChargeMetadata}
import org.sailcbi.APIServer.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.ApexUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority

class GetLocalStripeChargesForClose(closeId: Int) (implicit PA: PermissionsAuthority) extends HardcodedQueryForSelect[Charge](Set(ApexUserType), true) {
	val getQuery: String =
		s"""
		   |select CHARGE_ID, AMOUNT_IN_CENTS, CLOSE_ID, ORDER_ID, token, CREATED_EPOCH, PAID, status, refunds
		   |from STRIPE_CHARGES
		   |where CLOSE_ID = $closeId
		   |order by created_datetime
		   |
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSet): Charge = {
		// TODO: put this in a library somewhere
		val refunds = rs.getString(9) match {
			case "null" | null => None
			case s: String => Some(s)
		}

		val cmd = ChargeMetadata(
			closeId = Some(rs.getInt(3).toString),
			orderId = Some(rs.getInt(4).toString),
			token = Some(rs.getString(5)),
			cbiInstance = PA.instanceName.peek,
			refunds = refunds
		)

		Charge(
			id = rs.getString(1),
			amount = rs.getInt(2),
			metadata = cmd,
			created = rs.getInt(6),
			paid = rs.getString(7) == "Y",
			status = rs.getString(8)
		)
	}
}
