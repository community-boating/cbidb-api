package com.coleji.neptune.Util

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime, ZoneId, ZonedDateTime}

object DateUtil {
	val HOME_TIME_ZONE: ZoneId = ZoneId.of("America/New_York")
	val TIME_FORMAT = "HH:mm:ss"
	val DATE_FORMAT = "MM/dd/yyyy"
	val DATE_TIME_FORMAT = "MM/dd/yyyy  HH:mm:ss"
	val DATE_FORMAT_SQL = "MM/DD/YYYY"
	val DATE_TIME_FORMAT_SQL = "MM/DD/YYYY HH24:MI:SS"
	val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT)
	val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
	val TIME_FORMATTER: DateTimeFormatter  = DateTimeFormatter.ofPattern(TIME_FORMAT)

	def toDateSQL(startDate: LocalDate): String = {
		s"""to_date('${startDate.format(DateUtil.DATE_FORMATTER)}','$DATE_FORMAT_SQL')"""
	}

	def toDateSQL(startDatetime: LocalDateTime): String = {
		s"""to_date('${startDatetime.format(DateUtil.DATE_TIME_FORMATTER)}','$DATE_TIME_FORMAT_SQL')"""
	}

	def parse(dateLiteral: String, formatString: String = DATE_FORMAT): LocalDate =
		LocalDate.parse(dateLiteral, DateTimeFormatter.ofPattern(formatString))

	def betweenInclusive(test: LocalDate, start: LocalDate, end: LocalDate): Boolean = {
		if (end.isBefore(start)) throw new Exception("Call to DateUtil.betweenInclusive with start after end")

		test.isEqual(start) ||
			test.isEqual(end) || (
			test.isAfter(start) && test.isBefore(end)
			)
	}

	def parseWithDefault(literal: Option[String], default: LocalDate = LocalDate.now): LocalDate = {
		literal match {
			case None => default
			case Some(d) => {
				"^[0-9]{2}\\/[0-9]{2}\\/[0-9]{4}$".r.findFirstIn(d)
				match {
					case Some(_) => LocalDate.parse(d, DATE_FORMATTER)
					case None => default
				}
			}
		}
	}

	def toBostonTime(ldt: LocalDateTime): ZonedDateTime = {
		if (ldt == null) null
		else ldt.atZone(HOME_TIME_ZONE)
	}

	def toBostonTime(ld: LocalDate): ZonedDateTime = {
		if (ld == null) null
		else ld.atStartOfDay(HOME_TIME_ZONE)
	}

	def getTimestamp(ldt: LocalDateTime): Long = toBostonTime(ldt).toEpochSecond
}
