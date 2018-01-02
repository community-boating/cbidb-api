package Logic

import java.time.temporal.ChronoUnit
import java.time.{LocalDate, LocalDateTime, Month}

import CbiUtil.DateUtil
import Entities.EntityDefinitions.{MembershipType, MembershipTypeExp}
import Services.{RequestCache, ServerStateContainer}

class DateLogic(rc: RequestCache) {
  // TODO: also hardcoded in apex.  Need a data solution for spring/fall, and # of regular weeks
  lazy val getJpWeekAlias: (LocalDate => Option[String]) = {
    def isFall(d: LocalDate): Boolean =
      DateUtil.betweenInclusive(d, DateUtil.parse("09/02/2013"), DateUtil.parse("09/29/2013")) ||
      DateUtil.betweenInclusive(d, DateUtil.parse("09/02/2013"), DateUtil.parse("09/29/2013")) ||
      DateUtil.betweenInclusive(d, DateUtil.parse("09/02/2013"), DateUtil.parse("09/29/2013")) ||
      DateUtil.betweenInclusive(d, DateUtil.parse("09/02/2013"), DateUtil.parse("09/29/2013")) ||
      DateUtil.betweenInclusive(d, DateUtil.parse("09/02/2013"), DateUtil.parse("09/29/2013"))

    def isSpring(d: LocalDate): Boolean =
      DateUtil.betweenInclusive(d, DateUtil.parse("04/21/2014"), DateUtil.parse("05/25/2014")) ||
      DateUtil.betweenInclusive(d, DateUtil.parse("04/21/2014"), DateUtil.parse("05/25/2014")) ||
      DateUtil.betweenInclusive(d, DateUtil.parse("04/21/2014"), DateUtil.parse("05/25/2014")) ||
      DateUtil.betweenInclusive(d, DateUtil.parse("04/21/2014"), DateUtil.parse("05/25/2014"))

    def checkOffseason(d: LocalDate): Option[String] = {
      if (isFall(d)) Some("Fall")
      else if (isSpring(d)) Some("Spring")
      else None
    }

    val exps = rc.cachedEntities.membershipTypeExps.filter(
      _.references.membershipType.get.getID == MembershipType.specialIDs.MEM_TYPE_ID_JUNIOR_SUMMER
    )

    val yearToStartDate: Map[Int, (LocalDate => Option[String])] = exps.map(exp => (exp.values.season.get, {
      val startDate = exp.values.startDate.get
      (d: LocalDate) => {
        val daysBetween: Long = ChronoUnit.DAYS.between(startDate, d)
        val week: Long = daysBetween / 7
        if (daysBetween >= 0 && week >= 0 && week <= 9) Some("Week " + (week.toInt + 1).toString)
        else checkOffseason(d)
      }
    })).toMap


    (d: LocalDate) => yearToStartDate.get(d.getYear) match {
      case Some(f) => f(d)
      case None => checkOffseason(d)
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
