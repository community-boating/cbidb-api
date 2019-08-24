package org.sailcbi.APIServer.CbiUtil

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object GetSQLLiteral {
	def apply(s: String): String = "'" + GetSQLLiteralPrepared(s) + "'"

	def apply(s: Option[String]): String = GetSQLLiteralPrepared(s)

	def apply(i: Int): String = GetSQLLiteralPrepared(i)

	def apply(b: Boolean): String = "'" + GetSQLLiteralPrepared(b) + "'"

	def apply(zdt: ZonedDateTime, truncateToDay: Boolean = false): String = GetSQLLiteralPrepared(zdt, truncateToDay)
}
