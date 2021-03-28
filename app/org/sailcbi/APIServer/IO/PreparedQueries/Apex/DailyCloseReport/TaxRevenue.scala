package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import com.coleji.framework.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.UserTypes.{ApexRequestCache, StaffRequestCache}
import org.sailcbi.APIServer.Services.ResultSetWrapper

class TaxRevenue(closeId: Int) extends HardcodedQueryForSelect[TaxRevenueResult](Set(StaffRequestCache, ApexRequestCache)) {
	val taxDiscrepanciesId = MagicIds.FO_ITEM_TAX_DISCREPANCIES
	val getQuery: String =
		s"""
		   |select
		   |$closeId,
		   |nvl(sum(tax),0)
		   |from (
		   |  select sum(nvl(tax_amount,0)) as tax from close_summary where close_id = $closeId
		   | 	union all
		   | 	select sum(nvl(total_price,0)) from fo_misc where close_id = $closeId and item_id = $taxDiscrepanciesId
		   |)
		   |
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): TaxRevenueResult = new TaxRevenueResult(
		rs.getInt(1),
		Currency.dollars(rs.getDouble(2))
	)
}

class TaxRevenueResult(
							  val closeId: Int,
							  val total: Currency
					  )