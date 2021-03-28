package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import com.coleji.framework.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.Services.Authentication.{ApexRequestCache, StaffRequestCache}
import org.sailcbi.APIServer.Services.ResultSetWrapper

class CloseCash(closeId: Int) extends HardcodedQueryForSelect[CloseCashResult](Set(StaffRequestCache, ApexRequestCache)) {
	val getQuery: String =
		s"""
		   |select
		   |c.close_id,
		   |l.location_name_short,
		   |l.open,
		   |d.denomination * 100,
		   |c.value * 100,
		   |d.bill_coin,
		   |c.location_index,
		   |c.denom_index
		   |from fo_cash c, fo_cash_locations l, fo_cash_denominations d
		   |where c.location_index = l.location_index
		   |and c.DENOM_INDEX = d.DENOM_INDEX
		   |and c.close_id = $closeId
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): CloseCashResult = new CloseCashResult(
		rs.getInt(1),
		rs.getStringOrEmptyString(2),
		rs.getString(3) == "O",
		rs.getInt(4),
		Currency.cents(rs.getInt(5)),
		rs.getString(6) == "B"
	)
}

class CloseCashResult(
							 val closeId: Int,
							 val locationShort: String,
							 val isOpen: Boolean,
							 val denominationCents: Int,
							 val value: Currency,
							 val isBill: Boolean
					 )