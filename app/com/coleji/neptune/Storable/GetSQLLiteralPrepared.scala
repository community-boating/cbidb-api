package com.coleji.neptune.Storable

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, ZonedDateTime}

/**
 * For use with Prepared Statements only. Do not inject these directly into SQL!
 */
object GetSQLLiteralPrepared {
	def apply(s: String): String = if (s == "") null else s

	def apply(s: Option[String]): String = s match {
		case Some(x) => apply(x);
		case None => null
	}

	def apply(i: Int): String = i.toString

	def apply(b: Boolean): String = if (b) "Y" else "N"

	def apply(ld: LocalDate): String = {
		"TO_DATE('" + ld.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + "', 'MM/DD/YYYY')"
	}

	def apply(zdt: ZonedDateTime, truncateToDay: Boolean = false): String = {
		if (truncateToDay) "TO_DATE('" + zdt.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + "', 'MM/DD/YYYY')"
		else "TO_DATE('" + zdt.format(DateTimeFormatter.ofPattern("MM/dd/yyyy  HH:mm:ss")) + "', 'MM/DD/YYYY HH24:MI:SS')"
	}
}
