package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import com.coleji.neptune.IO.PreparedQueries.HardcodedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import com.coleji.neptune.Util.Currency
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.UserTypes.{ApexRequestCache, StaffRequestCache}

class SageRevenue(closeId: Int) extends HardcodedQueryForSelect[SageRevenueResult](Set(StaffRequestCache, ApexRequestCache)) {
	val sageSourceID: String = MagicIds.FO_AR_SOURCE_SAGE.toString
	val getQuery: String =
		s"""
		   |select
		   |close_id,
		   |nvl(sum(value),0)
		   |from fo_ar
		   |where close_id = $closeId
		   |and source_id =($sageSourceID )
		   |group by close_id
		   |
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): SageRevenueResult = new SageRevenueResult(
		rs.getInt(1),
		Currency.dollars(rs.getDouble(2))
	)
}

class SageRevenueResult(
							   val closeId: Int,
							   val total: Currency
					   )