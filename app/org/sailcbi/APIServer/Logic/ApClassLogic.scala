package org.sailcbi.APIServer.Logic

import com.coleji.neptune.Core.{RequestCache, UnlockedRequestCache}
import com.coleji.neptune.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import com.coleji.neptune.Util.DateUtil
import org.sailcbi.APIServer.Entities.EntityDefinitions.{DatetimeRange, SunsetTime}
import org.sailcbi.APIServer.Entities.cacheable.DatetimeRange.{DatetimeRangeCache, DatetimeRangeCacheKey}
import org.sailcbi.APIServer.Entities.cacheable.sunset.{SunsetCache, SunsetCacheKey}
import org.sailcbi.APIServer.Entities.cacheable.yearlydate.{YearlyDateAndItemCache, YearlyDateAndItemCacheKey, YearlyDateItemCache, YearlyDateItemCacheKey}
import org.sailcbi.APIServer.UserTypes.MemberRequestCache

import java.time.format.DateTimeFormatter
import java.time.{DayOfWeek, Duration, LocalDate, LocalDateTime}

class GuidedSailSlotDTO(day: String, start: String, end: String)

object ApClassLogic {
	val AP_GUIDED_SAIL_DURATION = Duration.ofMinutes(90)
	val AP_GUIDED_SAIL_INCREMENT = Duration.ofMinutes(15)
	val DATETIME_RANGE_TYPE = "guided-sail-blackout"

	/*Returns a list of start and end times for guided sailing time slots on a specific day
	use above parameters to change duration and time between these slots
	will ignore times
	* */

	val ONE_DAY = Duration.ofDays(1)

	def getApGuidedSailTimeSlots(rc: RequestCache, forMonth: LocalDate): List[LocalDateTime] = {
		val forMonthStart = forMonth.withDayOfMonth(1)
		val forMonthEnd = forMonth.plusMonths(1).minusDays(1)
		//val sunsetTimes = getSunsetTimes(rc, forMonthStart, forMonthEnd)
		//val dateRanges = DatetimeRangeCache.get(rc, DatetimeRangeCacheKey(forMonthStart, forMonthEnd, DATETIME_RANGE_TYPE))
		//println("Date Ranges", dateRanges)
		//val sunsetsCached = SunsetCache.get(rc, SunsetCacheKey(2023, 10))
		//println("Sunset Cached" + sunsetsCached)
		YearlyDateAndItemCache.nuke(rc, YearlyDateAndItemCacheKey(2020, "JP_CLASS_SCHEDULE"))
		val yearlyDates = YearlyDateAndItemCache.get(rc, YearlyDateAndItemCacheKey(2020, "JP_CLASS_SCHEDULE"))
		println("Yearly", yearlyDates)
		//val startTimes = sunsetTimes.map(a => getStartTime(a.toLocalDate))
		//val blackoutRanges = getBlackoutRanges(rc, forMonthStart, forMonthEnd)
		//val times = sunsetTimes.map(sunset => createDateRangesWithBlackouts(sunset.withHour(getStartTime(sunset.toLocalDate)), sunset, AP_GUIDED_SAIL_DURATION, AP_GUIDED_SAIL_INCREMENT, blackoutRanges))
		List.empty
	}

	def getApGuidedSailTimeSlotsForDay(rc: RequestCache, forDate: LocalDate): List[LocalDateTime] = {
		if (!programOpen(forDate)) List.empty
		else {
			val sunsetTime = Option(LocalDateTime.now())//getSunsetTime(rc, forDate)
			val startTime = getStartTime(forDate)
			val blackoutRanges = getBlackoutRanges(rc, forDate, forDate)
			forDate.atTime(startTime / 60, 0)
			sunsetTime.map(sunset => createDateRangesWithBlackouts(
				forDate.atTime(startTime / 60, 0),
				sunset,
				AP_GUIDED_SAIL_DURATION,
				AP_GUIDED_SAIL_INCREMENT,
				blackoutRanges
			)).getOrElse(List.empty)
		}
	}

	def getCurrentProgramStarts(rc: RequestCache, yearStart: LocalDate, yearEnd: LocalDate): Unit = {
		val q = new PreparedQueryForSelect[(LocalDateTime, LocalDateTime)](Set(MemberRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (LocalDateTime, LocalDateTime) = (rsw.getLocalDateTime(1), rsw.getLocalDateTime(2))
			override def getQuery: String =
				s"""
				  |select a.item_alias, b.start_date, b.end_date
					|from yearly_date_items a inner join yearly_dates b
					|on a.item_id = b.item_id
					|and a.item_alias = "butt"
				  |from DATETIME_RANGES where RANGE_TYPE = \'$DATETIME_RANGE_TYPE\'
				  |""".stripMargin
		}
		rc.executePreparedQueryForSelect(q)
	}

	private def getStartTime(forDate: LocalDate): Int = if (forDate.getDayOfWeek.getValue > DayOfWeek.FRIDAY.getValue) 9*60 else 13*60

	private def getSunsetTimes(rc: RequestCache, startDate: LocalDate, endDate: LocalDate): List[LocalDateTime] = {
		val q = new PreparedQueryForSelect[LocalDateTime](Set(MemberRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): LocalDateTime = rsw.getLocalDateTime(1)

			override def getQuery: String =
				s"""
				  |select SUNSET from SUNSET_TIMES where FOR_DATE >= ${DateUtil.toDateSQL(startDate)}
					|and FOR_DATE <= ${DateUtil.toDateSQL(endDate)}
				  |""".stripMargin
		}

		rc.executePreparedQueryForSelect(q)
	}

	private def getSunsetTime(rc: RequestCache, forDate: LocalDate): Option[LocalDateTime] = {
		val q = new PreparedQueryForSelect[LocalDateTime](Set(MemberRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): LocalDateTime = rsw.getLocalDateTime(1)

			override def getQuery: String =
				s"""
				  |select SUNSET from SUNSET_TIMES where FOR_DATE = ${DateUtil.toDateSQL(forDate)}
				  |""".stripMargin
		}

		rc.executePreparedQueryForSelect(q).headOption
	}

	private def getBlackoutRanges(rc: RequestCache, startDate: LocalDate, endDate: LocalDate): List[(LocalDateTime, LocalDateTime)] = {
		val startDateSQL = DateUtil.toDateSQL(startDate)
		val endDateSQL = DateUtil.toDateSQL(endDate)
		val q = new PreparedQueryForSelect[(LocalDateTime, LocalDateTime)](Set(MemberRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (LocalDateTime, LocalDateTime) = (rsw.getLocalDateTime(1), rsw.getLocalDateTime(2))
			override def getQuery: String =
				s"""
				  |select START_DATETIME, END_DATETIME
				  |from DATETIME_RANGES where RANGE_TYPE = \'$DATETIME_RANGE_TYPE\'
				  |and ((START_DATETIME >= $startDateSQL
					|and START_DATETIME <= $endDateSQL)
					|or (END_DATETIME >= $startDateSQL
					|and END_DATETIME <= $endDateSQL)
					|or (START_DATETIME <= $startDateSQL
					|and END_DATETIME >= $endDateSQL))
				  |""".stripMargin
		}
		rc.executePreparedQueryForSelect(q)
	}

	private def createDateRanges(start: LocalDateTime, end: LocalDateTime, duration: Duration, increment: Duration): List[LocalDateTime] = {
		val count = Duration.between(start, end.minus(duration)).dividedBy(increment).floor.intValue
		(0 to count).map(i => start.plus(increment.multipliedBy(i))).toList
	}

	private def createDateRangesWithBlackouts(
		start: LocalDateTime, end: LocalDateTime, duration: Duration, increment: Duration, blackouts: List[(LocalDateTime, LocalDateTime)]
	): List[LocalDateTime] = {
		if (end.toLocalDate != start.toLocalDate) List.empty
		else {
			val starts = createDateRanges(start, end, duration, increment)
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
