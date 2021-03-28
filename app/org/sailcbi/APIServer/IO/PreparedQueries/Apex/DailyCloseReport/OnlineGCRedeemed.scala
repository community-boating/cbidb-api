package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import com.coleji.framework.IO.PreparedQueries.HardcodedQueryForSelect
import com.coleji.framework.Storable.ResultSetWrapper
import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.UserTypes.{ApexRequestCache, StaffRequestCache}

class OnlineGCRedeemed(closeId: Int) extends HardcodedQueryForSelect[OnlineGCRedeemedResult](Set(StaffRequestCache, ApexRequestCache)) {
	val getQuery: String =
		s"""
		   |select
		   |close_id,
		   |nvl(sum(value),0)
		   |from fo_applied_gc
		   |where close_id = $closeId
		   |and order_id is not null
		   |group by close_id
		   |
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): OnlineGCRedeemedResult = new OnlineGCRedeemedResult(
		rs.getInt(1),
		Currency.dollars(rs.getDouble(2))
	)
}

class OnlineGCRedeemedResult(
	val closeId: Int,
	val total: Currency
)