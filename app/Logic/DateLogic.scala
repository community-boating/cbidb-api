package Logic

import java.time.temporal.ChronoUnit
import java.time.{LocalDate, LocalDateTime, Month}

import Entities.EntityDefinitions.{MembershipType, MembershipTypeExp}
import Services.{RequestCache, ServerStateContainer}

class DateLogic(rc: RequestCache) {
  // TODO: also hardcoded in apex.  Need a data solution for spring/fall, and # of regular weeks
  lazy val getJpWeek: (LocalDate => Option[Int]) = {
    val exps = rc.cachedEntities.membershipTypeExps.filter(
      _.references.membershipType.get.getID == MembershipType.specialIDs.MEM_TYPE_ID_JUNIOR_SUMMER
    )
    val yearToStartDate: Map[Int, (LocalDate => Option[Int])] = exps.map(exp => (exp.values.season.get, {
      val startDate = exp.values.startDate.get
      (d: LocalDate) => {
        val daysBetween: Long = ChronoUnit.DAYS.between(startDate, d)
        val week: Long = daysBetween / 7
        if (week >= 0 && week <= 11) Some(week.toInt + 1)
        else None
      }
    })).toMap

    (d: LocalDate) => yearToStartDate.get(d.getYear) match {
      case Some(f) => f(d)
      case None => None
    }
  }
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
