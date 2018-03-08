package Logic.PreparedQueries.Apex

import java.sql.ResultSet

import Api.ApiDataObject
import Logic.PreparedQueries.PreparedQuery
import Services.Authentication.{ApexUserType, UserType}

class GetCurrentOnlineClose extends PreparedQuery[GetCurrentOnlineCloseResult]{
  override val allowedUserTypes: Set[UserType] = Set(ApexUserType)
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
) extends ApiDataObject