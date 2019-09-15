package CbiUtil

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object GetSQLLiteral {
	def apply(s: String): String = "'" + s.replace("'", "''") + "'"

	def apply(s: Option[String]): String = s match {
		case Some(x) => apply(x);
		case None => "null"
	}

	def apply(i: Int): String = i.toString

	def apply(b: Boolean): String = if (b) "'Y'" else "'N'"

	def apply(zdt: ZonedDateTime, truncateToDay: Boolean = false): String = {
		if (truncateToDay) "TO_DATE('" + zdt.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + "', 'MM/DD/YYYY')"
		else "TO_DATE('" + zdt.format(DateTimeFormatter.ofPattern("MM/dd/yyyy  HH:mm:ss")) + "', 'MM/DD/YYYY HH24:MI:SS')"
	}
}
