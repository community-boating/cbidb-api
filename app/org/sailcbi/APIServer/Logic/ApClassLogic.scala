package org.sailcbi.APIServer.Logic

import com.coleji.neptune.Core.{RequestCache, UnlockedRequestCache}
import com.coleji.neptune.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import com.coleji.neptune.Util.DateUtil
import org.sailcbi.APIServer.Entities.EntityDefinitions.{DatetimeRange, SunsetTime}
import org.sailcbi.APIServer.UserTypes.MemberRequestCache

import java.time.{DayOfWeek, Duration, LocalDate, LocalDateTime}

object ApClassLogic {
	val AP_GUIDED_SAIL_DURATION = Duration.ofMinutes(90)
	val AP_GUIDED_SAIL_INCREMENT = Duration.ofMinutes(15)
	val DATETIME_RANGE_TYPE = "guided-sail-blackout"

	def getApGuidedSailTimeslots(rc: RequestCache, forDate: LocalDate): List[LocalDateTime] = {
		if (!programOpen(forDate)) List.empty
		else {
			val sunsetTime = getSunsetTime(rc, forDate)

			val startTime = if (forDate.getDayOfWeek.getValue > DayOfWeek.FRIDAY.getValue) 9 else 13

			val blackoutRanges = getBlackoutRanges(rc, forDate)

			forDate.atTime(startTime, 0)

			sunsetTime.map(sunset => createDateRanges(
				forDate.atTime(startTime, 0),
				sunset,
				AP_GUIDED_SAIL_DURATION,
				AP_GUIDED_SAIL_INCREMENT,
				blackoutRanges
			)).getOrElse(List.empty)
		}
	}

	private def getSunsetTime(rc: RequestCache, forDate: LocalDate): Option[LocalDateTime] = {
		val q = new PreparedQueryForSelect[LocalDateTime](Set(MemberRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): LocalDateTime = rsw.getLocalDateTime(1)

			override def getQuery: String =
				s"""
				  |select SUNSET from SUNSET_TIMES where FOR_DATE = to_date('${forDate.format(DateUtil.DATE_FORMATTER)}','MM/DD/YYYY')
				  |""".stripMargin
		}

		rc.executePreparedQueryForSelect(q).headOption
	}

	private def getBlackoutRanges(rc: RequestCache, forDate: LocalDate): List[(LocalDateTime, LocalDateTime)] = {
		val q = new PreparedQueryForSelect[(LocalDateTime, LocalDateTime)](Set(MemberRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (LocalDateTime, LocalDateTime) = (rsw.getLocalDateTime(1), rsw.getLocalDateTime(2))

			override def getQuery: String =
				s"""
				  |select START_DATETIME, END_DATETIME
				  |from DATETIME_RANGES where RANGE_TYPE = $DATETIME_RANGE_TYPE
				  |and trunc(START_DATETIME) = to_date('${forDate.format(DateUtil.DATE_FORMATTER)}','MM/DD/YYYY')
				  |""".stripMargin
		}

		rc.executePreparedQueryForSelect(q)
	}

	private def createDateRanges(
		start: LocalDateTime, end: LocalDateTime, duration: Duration, increment: Duration, blackouts: List[(LocalDateTime, LocalDateTime)]
	): List[LocalDateTime] = {
		if (end.toLocalDate != start.toLocalDate) List.empty
		else {
			val count = Duration.between(start, end.minus(duration)).dividedBy(increment).floor.intValue
			val starts = (0 to count).map(i => start.plus(increment.multipliedBy(i))).toList

			starts.map(s => (s, s.plus(duration))).filter(r => {
				!blackouts.exists(b => rangesOverlap(false, r, b))
			}).map(_._1)
		}
	}

	// april-oct except for 7/4
	private def programOpen(forDate: LocalDate): Boolean = {
		val monthInt = forDate.getMonthValue
		val isJulyFourth = monthInt == 7 && forDate.getDayOfMonth == 4

		!isJulyFourth && monthInt >= 4 && monthInt <= 10
	}

	private def rangesOverlap(tangentCounts: Boolean, r1: (LocalDateTime, LocalDateTime), r2: (LocalDateTime, LocalDateTime)): Boolean = {
		if (r1._1 == r2._1 || r1._2 == r2._2) true // they start (or end) at the same time
		else {
			// they dont start at the same time
			val (earlierStart, laterStart) = if (r1._1.isBefore(r2._1)) (r1, r2) else (r2, r1)
			if (earlierStart._2 == laterStart._1) tangentCounts // they are tangent
			else laterStart._1.isAfter(earlierStart._1) && laterStart._1.isBefore(earlierStart._2) // second range start time is inside first range
		}
	}

//	def main(args: Array[String]): Unit = {
//		println(createDateRanges(
//			LocalDate.now().atTime(9, 0),
//			LocalDate.now().atTime(19, 35),
//			AP_GUIDED_SAIL_DURATION,
//			AP_GUIDED_SAIL_INCREMENT,
//			List((LocalDate.now().atTime(12, 0), LocalDate.now().atTime(13, 30)))
//		))
//	}
}
