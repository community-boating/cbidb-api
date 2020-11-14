package org.sailcbi.APIServer.CbiUtil

import java.time.{LocalDate, ZonedDateTime}

/**
 * For injecting values directly into SQL.  NOT INJECTION-SAFE; NEVER USE THESE METHODS FOR DATA FROM USERS!
 */
object GetSQLLiteral {
	/** NOT INJECTION SAFE, DO NOT USE WITH USER DATA */
	def apply(s: String): String = "'" + GetSQLLiteralPrepared(s).replace("'", "''") + "'"

	/** NOT INJECTION SAFE, DO NOT USE WITH USER DATA */
	def apply(s: Option[String]): String = GetSQLLiteralPrepared(s) match {
		case null => "null"
		case s: String => apply(s)
	}

	def apply(i: Int): String = GetSQLLiteralPrepared(i)

	def apply(b: Boolean): String = "'" + GetSQLLiteralPrepared(b) + "'"

	def apply(ld: LocalDate): String = GetSQLLiteralPrepared(ld)

	def apply(zdt: ZonedDateTime, truncateToDay: Boolean = false): String = GetSQLLiteralPrepared(zdt, truncateToDay)
}
