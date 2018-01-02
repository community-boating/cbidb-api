package Logic

import java.time.{LocalDate, LocalDateTime, Month}

import Services.{RequestCache, ServerStateContainer}

class DateLogic(rc: RequestCache) {
  lazy val getJpWeek: (LocalDate => Option[Int]) = ???
}

object DateLogic {
  def now: LocalDateTime = LocalDateTime.now.plusSeconds(ServerStateContainer.get.serverTimeOffsetSeconds)

  def currentSeason(asOf: LocalDate = now.toLocalDate): Int = {
    val currentYear = asOf.getYear
    asOf.getMonth match {
      case Month.NOVEMBER | Month.DECEMBER => currentYear + 1
      case _ => currentYear
    }
  }
}
