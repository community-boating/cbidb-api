package org.sailcbi.APIServer.CbiUtil

import java.time.ZonedDateTime

object GetSQLLiteral {
	def apply(s: String): String = "'" + GetSQLLiteralPrepared(s).replace("'", "''") + "'"

	def apply(s: Option[String]): String = GetSQLLiteralPrepared(s) match {
		case null => "null"
		case s: String => apply(s)
	}

	def apply(i: Int): String = GetSQLLiteralPrepared(i)

	def apply(b: Boolean): String = "'" + GetSQLLiteralPrepared(b) + "'"

	def apply(zdt: ZonedDateTime, truncateToDay: Boolean = false): String = GetSQLLiteralPrepared(zdt, truncateToDay)
}
