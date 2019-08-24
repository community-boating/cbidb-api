package org.sailcbi.APIServer.Services

import java.sql.ResultSet

class ResultSetWrapper(rs: ResultSet) {
	def getOptionString(c: Int): Option[String] = {
		val ret = rs.getString(c)
		if (rs.wasNull()) None
		else Some(ret)
	}

	def getStringOrEmptyString(c: Int): String = {
		getOptionString(c) match {
			case Some(s) => s
			case None => ""
		}
	}

	def getOptionInt(c: Int): Option[Int] = {
		val ret = rs.getInt(c)
		if (rs.wasNull()) None
		else Some(ret)
	}

	def getBooleanFromChar(c: Int): Boolean = {
		val ret = rs.getString(c)
		if (ret == "Y") true
		else if (ret == "N") false
		else throw new Exception(s"Unexpected booleanish char '${ret}'")
	}
}

object ResultSetWrapper {
	implicit def wrapResultSet(rs: ResultSet): ResultSetWrapper = new ResultSetWrapper(rs)
}