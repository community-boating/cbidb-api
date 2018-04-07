package IO.PreparedQueries.Apex.DailyCloseReport

import java.sql.ResultSet

import CbiUtil.Currency
import Entities.MagicIds
import IO.PreparedQueries.HardcodedQueryForSelect
import Services.Authentication.ApexUserType

class SageRevenue(closeId: Int) extends HardcodedQueryForSelect[SageRevenueResult](Set(ApexUserType)) {
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

  override def mapResultSetRowToCaseObject(rs: ResultSet): SageRevenueResult = new SageRevenueResult(
    rs.getInt(1),
    Currency.dollars(rs.getDouble(2))
  )
}

class SageRevenueResult (
  val closeId: Int,
  val total: Currency
)