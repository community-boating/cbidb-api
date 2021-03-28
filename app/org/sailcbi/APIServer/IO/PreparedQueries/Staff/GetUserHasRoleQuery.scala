package org.sailcbi.APIServer.IO.PreparedQueries.Staff

import com.coleji.framework.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.StaffRequestCache
import org.sailcbi.APIServer.Services.ResultSetWrapper

class GetUserHasRoleQuery(userName: String, roleAlias: String) extends PreparedQueryForSelect[Boolean](Set(StaffRequestCache)) {

	val getQuery: String =
		s"""
		   |select role_pkg.has_permission(?, ?) from dual
		   |
    """.stripMargin

	override val params: List[String] = List(
		userName.toUpperCase(), roleAlias.toUpperCase()
	)

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Boolean = rs.getString(1) == "Y"
}
