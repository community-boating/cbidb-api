package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import com.coleji.framework.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.UserTypes.{ApexRequestCache, StaffRequestCache}
import org.sailcbi.APIServer.Services.ResultSetWrapper

class InPersonOtherARTotal(closeId: Int) extends HardcodedQueryForSelect[InPersonOtherARTotalResult](Set(StaffRequestCache, ApexRequestCache)) {
	val sourceIDs: String = MagicIds.FO_INPERSON_CC_SOURCE_IDs.mkString(", ")
	val sageSourceID: String = MagicIds.FO_AR_SOURCE_SAGE.toString
	val getQuery: String =
		s"""
		   |select
		   |ar.close_id,
		   |nvl(sum(ar.value),0)
		   |from fo_ar ar
		   |where ar.close_id = $closeId
		   |and ar.source_id not in ($sourceIDs )
		   |and ar.source_id <> $sageSourceID
		   |group by ar.close_id
		   |
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): InPersonOtherARTotalResult = new InPersonOtherARTotalResult(
		rs.getInt(1),
		Currency.dollars(rs.getDouble(2))
	)
}

class InPersonOtherARTotalResult(
	val closeId: Int,
	val total: Currency
)