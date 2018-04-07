package IO.PreparedQueries.Apex.DailyCloseReport

import java.sql.ResultSet

import CbiUtil.Currency
import IO.PreparedQueries.HardcodedQueryForSelect
import Services.Authentication.ApexUserType

class OnlineGCRedeemed(closeId: Int) extends HardcodedQueryForSelect[OnlineGCRedeemedResult](Set(ApexUserType)) {
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

  override def mapResultSetRowToCaseObject(rs: ResultSet): OnlineGCRedeemedResult = new OnlineGCRedeemedResult(
    rs.getInt(1),
    Currency.dollars(rs.getDouble(2))
  )
}

class OnlineGCRedeemedResult (
  val closeId: Int,
  val total: Currency
)