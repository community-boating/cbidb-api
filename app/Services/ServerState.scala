package Services

import java.time.{LocalDate, LocalDateTime, Month}

case class ServerState(pa: PermissionsAuthority, serverTimeOffsetSeconds: Long) {
  def now: LocalDateTime = LocalDateTime.now.plusSeconds(serverTimeOffsetSeconds)

  def currentSeason(asOf: LocalDate = now.toLocalDate): Int = {
    val currentYear = now.getYear
    now.getMonth match {
      case Month.NOVEMBER | Month.DECEMBER => currentYear + 1
      case _ => currentYear
    }
  }
}