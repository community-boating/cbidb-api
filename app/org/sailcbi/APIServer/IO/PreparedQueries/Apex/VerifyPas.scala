package org.sailcbi.APIServer.IO.PreparedQueries.Apex

import com.coleji.neptune.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import org.sailcbi.APIServer.UserTypes.ApexRequestCache

class VerifyPas(userName: String, pas: String, procName: String, argString: String) extends PreparedQueryForSelect[Boolean](Set(ApexRequestCache)) {
	val getQuery: String =
		s"""
		   |select verify_pas(?, ?, ?, ?) from dual
		   |
    """.stripMargin

	override val params: List[String] = List(userName, pas, procName, argString)

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Boolean = rs.getString(1) == "Y"
}
