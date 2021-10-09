package com.coleji.neptune.Storable

import java.time.{LocalDate, ZonedDateTime}

object GetSQLLiteral {
	def apply(i: Int): String = GetSQLLiteralPrepared(i)

	def apply(b: Boolean): String = "'" + GetSQLLiteralPrepared(b) + "'"

	def apply(ld: LocalDate): String = GetSQLLiteralPrepared(ld)

	def apply(zdt: ZonedDateTime, truncateToDay: Boolean = false): String = GetSQLLiteralPrepared(zdt, truncateToDay)
}
