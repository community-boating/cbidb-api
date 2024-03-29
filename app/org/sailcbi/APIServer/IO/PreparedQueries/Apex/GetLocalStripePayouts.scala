package org.sailcbi.APIServer.IO.PreparedQueries.Apex

import com.coleji.neptune.IO.PreparedQueries.HardcodedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import com.coleji.neptune.Util.DateUtil
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.Payout
import org.sailcbi.APIServer.UserTypes.ApexRequestCache

class GetLocalStripePayouts extends HardcodedQueryForSelect[Payout](Set(ApexRequestCache), true) {
	val getQuery: String =
		s"""
		   |select PAYOUT_ID, AMOUNT_IN_CENTS, ARRIVAL_DATETIME, BALANCE_TRANSACTION_ID, STATUS
		   |from STRIPE_PAYOUTS
		   |order by ARRIVAL_DATETIME
		   |
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Payout = Payout(
		id = rs.getString(1),
		amount = rs.getInt(2),
		arrival_date = rs.getLocalDateTime(3).atZone(DateUtil.HOME_TIME_ZONE).toEpochSecond,
		balance_transaction = rs.getString(4),
		status = rs.getString(5)
	)
}
