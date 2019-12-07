package org.sailcbi.APIServer.IO.PreparedQueries.Apex

import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.ApexUserType
import org.sailcbi.APIServer.Services.ResultSetWrapper

class VerifyPas(userName: String, pas: String, procName: String, argString: String) extends PreparedQueryForSelect[Boolean](Set(ApexUserType)) {
	val getQuery: String =
		s"""
		   |select verify_pas(?, ?, ?, ?) from dual
		   |
    """.stripMargin

	override val params: List[String] = List(userName, pas, procName, argString)

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Boolean = rs.getString(1) == "Y"
}
