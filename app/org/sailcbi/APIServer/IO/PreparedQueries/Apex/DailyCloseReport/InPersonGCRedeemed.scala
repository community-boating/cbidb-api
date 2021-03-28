package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import com.coleji.framework.IO.PreparedQueries.HardcodedQueryForSelect
import com.coleji.framework.Storable.ResultSetWrapper
import com.coleji.framework.Util.Currency
import org.sailcbi.APIServer.UserTypes.{ApexRequestCache, StaffRequestCache}

class InPersonGCRedeemed(closeId: Int) extends HardcodedQueryForSelect[InPersonGCRedeemedResult](Set(StaffRequestCache, ApexRequestCache)) {
	val getQuery: String =
		s"""
		   |select
		   |close_id,
		   |nvl(sum(value),0)
		   |from fo_applied_gc
		   |where close_id = $closeId
		   |and order_id is null
		   |group by close_id
		   |
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): InPersonGCRedeemedResult = new InPersonGCRedeemedResult(
		rs.getInt(1),
		Currency.dollars(rs.getDouble(2))
	)
}

class InPersonGCRedeemedResult(
	val closeId: Int,
	val total: Currency
)