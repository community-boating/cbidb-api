package IO.PreparedQueries.Staff

import java.sql.ResultSet

import IO.PreparedQueries.PreparedQueryForSelect
import Services.Authentication.StaffUserType

class GetUserHasRoleQuery(userName: String, roleAlias: String) extends PreparedQueryForSelect[Boolean](Set(StaffUserType)) {

  val getQuery: String =
    s"""
       |select role_pkg.has_permission(?, ?) from dual
       |
    """.stripMargin

  override val params: List[String] = List(
    userName.toUpperCase(), roleAlias.toUpperCase()
  )

  override def mapResultSetRowToCaseObject(rs: ResultSet): Boolean = rs.getString(1) == "Y"
}
