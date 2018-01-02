package CbiUtil

import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

}
