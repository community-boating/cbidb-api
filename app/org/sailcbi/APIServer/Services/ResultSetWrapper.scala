package org.sailcbi.APIServer.Services

import java.sql.ResultSet
import java.time.{LocalDate, LocalDateTime}

class ResultSetWrapper(rs: ResultSet) {
	def getOptionString(c: Int): Option[String] = getValue(_.getString, c)
	def getOptionInt(c: Int): Option[Int] = getValue(_.getInt, c)
	def getOptionDouble(c: Int): Option[Double] = getValue(_.getDouble, c)
	def getOptionLocalDate(c: Int): Option[LocalDate] = getValue(rs => cc => rs.getDate(cc).toLocalDate, c)
	def getOptionLocalDateTime(c: Int): Option[LocalDateTime] = getValue(rs => cc => rs.getTimestamp(cc).toLocalDateTime, c)

	@deprecated
	def getString(c: Int): String = getOptionString(c).orNull
	@deprecated
	// per javadoc on java.sql.ResultSet.getInt, if the value is SQL NULL then the method returns 0 since the method returns primitive ints
	// since that is totally unacceptable, we're throwing.
	def getInt(c: Int): Int = getOptionInt(c).get
	@deprecated
	// liekwise for doubles
	def getDouble(c: Int): Double = getOptionDouble(c).get
	@deprecated
	def getLocalDate(c: Int): LocalDate = getOptionLocalDate(c).orNull
	@deprecated
	def getLocalDateTime(c: Int): LocalDateTime = getOptionLocalDateTime(c).orNull

	def getStringOrEmptyString(c: Int): String = {
		getOptionString(c) match {
			case Some(s) => s
			case None => ""
		}
	}

	def getOptionBooleanFromChar(c: Int): Option[Boolean] = {
		getOptionString(c) match {
			case Some("Y") => Some(true)
			case Some("N") => Some(false)
			case None => None
			case Some(s) => throw new Exception(s"Unexpected booleanish char '${s}'")
		}
	}

	def getBooleanFromChar(c: Int): Boolean = getOptionBooleanFromChar(c).get

	private def getValue[T](get: ResultSet => Int => T, c: Int): Option[T] = {
		val ret = get(rs)(c)
		if (rs.wasNull()) None
		else Some(ret)
	}
}

object ResultSetWrapper {
//	implicit def wrapResultSet(rs: ResultSet): ResultSetWrapper = new ResultSetWrapper(rs)
	def apply(rs: ResultSet) = new ResultSetWrapper(rs)
}