package org.sailcbi.APIServer.IO.PreparedQueries.Staff

import com.coleji.neptune.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import org.sailcbi.APIServer.UserTypes.StaffRequestCache

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
