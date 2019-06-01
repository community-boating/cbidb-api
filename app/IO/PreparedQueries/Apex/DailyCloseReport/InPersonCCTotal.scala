package IO.PreparedQueries.Apex.DailyCloseReport

import java.sql.ResultSet

import CbiUtil.Currency
import Entities.MagicIds
import IO.PreparedQueries.HardcodedQueryForSelect
import Services.Authentication.ApexUserType

class InPersonCCTotal(closeId: Int) extends HardcodedQueryForSelect[InPersonCCTotalResult](Set(ApexUserType)) {
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

	override def mapResultSetRowToCaseObject(rs: ResultSet): InPersonCCTotalResult = new InPersonCCTotalResult(
		rs.getInt(1),
		Currency.dollars(rs.getDouble(2))
	)
}

class InPersonCCTotalResult(
								   val closeId: Int,
								   val ccTotal: Currency
						   )