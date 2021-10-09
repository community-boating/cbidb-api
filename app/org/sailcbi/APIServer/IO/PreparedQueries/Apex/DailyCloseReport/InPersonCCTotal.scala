package org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport

import com.coleji.neptune.IO.PreparedQueries.HardcodedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import com.coleji.neptune.Util.Currency
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.UserTypes.{ApexRequestCache, StaffRequestCache}

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