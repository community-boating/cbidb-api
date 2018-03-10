package IO.PreparedQueries.Apex

import java.sql.ResultSet

import IO.PreparedQueries.PreparedQueryForSelect
import Services.Authentication.ApexUserType

class GetCurrentOnlineClose extends PreparedQueryForSelect[GetCurrentOnlineCloseResult](Set(ApexUserType)) {
  val getQuery: String =
    s"""
       |select cc_pkg.get_current_online_close from dual
       |
    """.stripMargin

  override def mapResultSetRowToCaseObject(rs: ResultSet): GetCurrentOnlineCloseResult = GetCurrentOnlineCloseResult(
    rs.getInt(1)
  )
}

case class GetCurrentOnlineCloseResult (
  closeId: Int,
)