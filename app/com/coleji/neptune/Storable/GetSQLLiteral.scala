package com.coleji.neptune.Storable

import com.coleji.neptune.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PersistenceSystem}
import com.coleji.neptune.Util.DateUtil.HOME_TIME_ZONE

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime, ZonedDateTime}

object GetSQLLiteral {
	def apply(i: Int): String = i.toString
	def apply(d: Double): String = d.toString

	def apply(b: Boolean): String = "'" + GetSQLLiteralPrepared(b) + "'"

	def apply(ld: LocalDate)(implicit persistenceSystem: PersistenceSystem): String = persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => "'" + ld.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'"
		case PERSISTENCE_SYSTEM_ORACLE => "TO_DATE('" + ld.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + "', 'MM/DD/YYYY')"
	}

	def apply(ldt: LocalDateTime, truncateToDay: Boolean)(implicit persistenceSystem: PersistenceSystem): String = {
		if (truncateToDay) apply(ldt.toLocalDate)
		else persistenceSystem match {
			case PERSISTENCE_SYSTEM_MYSQL => "'" + ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "'"
			case PERSISTENCE_SYSTEM_ORACLE => "TO_DATE('" + ldt.format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")) + "', 'MM/DD/YYYY HH24:MI:SS')"
		}
	}

	def apply(zdt: ZonedDateTime, truncateToDay: Boolean)(implicit persistenceSystem: PersistenceSystem): String = apply(zdt.withZoneSameInstant(HOME_TIME_ZONE).toLocalDateTime, truncateToDay)

	def apply(o: Option[_]): String = o match{
		case None => null
		case Some(i: Int) => apply(i)
		case Some(d: Double) => apply(d)
		case Some(b: Boolean) => apply(b)
		case Some(ld: LocalDate) => apply(ld)
		case Some(ldt: LocalDateTime) => apply(ldt, truncateToDay = false)
		case _ => throw new Exception("Unexpected optioned type in GetSQLLiteral")
	}
}
