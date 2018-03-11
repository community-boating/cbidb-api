package CbiUtil

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime, ZoneId, ZonedDateTime}

object DateUtil {
  def parse(dateLiteral: String, formatString: String = "MM/dd/yyyy"): LocalDate =
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
          case Some(_) => LocalDate.parse(d, DateTimeFormatter.ofPattern("MM/dd/yyyy"))
          case None => default
        }
      }
    }
  }

  def toBostonTime(ldt: LocalDateTime): ZonedDateTime = ldt.atZone(ZoneId.of("America/New_York"))
  def toBostonTime(ld: LocalDate): ZonedDateTime = ld.atStartOfDay(ZoneId.of("America/New_York"))

  def getTimestamp(ldt: LocalDateTime): Long = toBostonTime(ldt).toEpochSecond
}
