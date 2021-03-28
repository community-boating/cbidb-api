package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import com.coleji.framework.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.Services.Authentication.{ApexRequestCache, StaffRequestCache}
import org.sailcbi.APIServer.Services.ResultSetWrapper

class InPersonCCTotal(closeId: Int) extends HardcodedQueryForSelect[InPersonCCTotalResult](Set(StaffRequestCache, ApexRequestCache)) {
	val sourceIDs: String = MagicIds.FO_INPERSON_CC_SOURCE_IDs.mkString(", ")
	val getQuery: String =
		s"""
		   |select
		   |close_id,
		   |nvl(sum(value),0)
		   |from fo_ar
		   |where close_id = $closeId
		   |and source_id in ($sourceIDs )
		   |group by close_id
		   |
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): InPersonCCTotalResult = new InPersonCCTotalResult(
		rs.getInt(1),
		Currency.dollars(rs.getDouble(2))
	)
}

class InPersonCCTotalResult(
	val closeId: Int,
	val ccTotal: Currency
)