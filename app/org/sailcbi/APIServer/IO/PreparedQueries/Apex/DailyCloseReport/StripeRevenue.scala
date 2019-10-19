package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.ApexUserType
import org.sailcbi.APIServer.Services.ResultSetWrapper

class StripeRevenue(closeId: Int) extends HardcodedQueryForSelect[StripeRevenueResult](Set(ApexUserType), true) {
	val getQuery: String =
		s"""
		   |select
		   |close_id,
		   |sum(amount_in_cents)
		   |from (
		   | select close_id, nvl(sum(amount_in_cents),0) as amount_in_cents from stripe_charges where close_id = $closeId and paid = 'Y' group by close_id
		   | union All
		   | select close_id, nvl(sum(-1 * amount_in_cents),0) as amount_in_cents from stripe_refunds where close_id = $closeId group by close_id
		   |)
		   |group by close_id
		   |
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): StripeRevenueResult = new StripeRevenueResult(
		rs.getInt(1),
		Currency.cents(rs.getInt(2))
	)
}

class StripeRevenueResult(
	val closeId: Int,
	val total: Currency
)