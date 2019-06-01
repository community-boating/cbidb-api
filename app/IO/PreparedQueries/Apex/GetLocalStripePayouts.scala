package IO.PreparedQueries.Apex

import java.sql.ResultSet

import CbiUtil.DateUtil
import Entities.JsFacades.Stripe.Payout
import IO.PreparedQueries.HardcodedQueryForSelect
import Services.Authentication.ApexUserType

class GetLocalStripePayouts extends HardcodedQueryForSelect[Payout](Set(ApexUserType), true) {
	val getQuery: String =
		s"""
		   |select PAYOUT_ID, AMOUNT_IN_CENTS, ARRIVAL_DATETIME, BALANCE_TRANSACTION_ID, STATUS
		   |from STRIPE_PAYOUTS
		   |order by ARRIVAL_DATETIME
		   |
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSet): Payout = Payout(
		id = rs.getString(1),
		amount = rs.getInt(2),
		arrival_date = rs.getTimestamp(3).toLocalDateTime.atZone(DateUtil.HOME_TIME_ZONE).toEpochSecond,
		balance_transaction = rs.getString(4),
		status = rs.getString(5)
	)
}
