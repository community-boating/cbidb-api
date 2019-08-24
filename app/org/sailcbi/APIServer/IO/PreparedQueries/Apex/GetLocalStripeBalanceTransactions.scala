package org.sailcbi.APIServer.IO.PreparedQueries.Apex

import java.sql.ResultSet

import org.sailcbi.APIServer.Entities.JsFacades.Stripe.{BalanceTransaction, Payout}
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.ApexUserType

class GetLocalStripeBalanceTransactions(payout: Payout) extends PreparedQueryForSelect[BalanceTransaction](Set(ApexUserType), true) {
	val getQuery: String =
		s"""
		   |select TRANSACTION_ID, AMOUNT_IN_CENTS, DESCRIPTION, FEE_IN_CENTS, NET_IN_CENTS, SOURCE, STATUS, TYPE
		   |from STRIPE_BALANCE_TRANSACTIONS
		   |where payout = ?
		   |
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSet): BalanceTransaction = BalanceTransaction(
		id = rs.getString(1),
		amount = rs.getInt(2),
		description = rs.getOptionString(3),
		fee = rs.getInt(4),
		net = rs.getInt(5),
		source = rs.getString(6),
		status = rs.getString(7),
		`type` = rs.getString(8),
		payout = payout.id
	)

	override val params: List[String] = List(payout.id)
}
