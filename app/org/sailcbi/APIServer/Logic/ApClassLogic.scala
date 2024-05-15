package org.sailcbi.APIServer.Logic

import com.coleji.neptune.Core.RequestCache
import com.coleji.neptune.IO.PreparedQueries.{PreparedQueryForInsert, PreparedQueryForSelect}
import com.coleji.neptune.Storable.ResultSetWrapper
import com.coleji.neptune.Storable.StorableQuery.QueryBuilder
import com.coleji.neptune.Util.DateUtil
import org.sailcbi.APIServer.Entities.EntityDefinitions.ApClassSession
import org.sailcbi.APIServer.Entities.cacheable.DatetimeRange.{DatetimeRangeCache, DatetimeRangeCacheKey}
import org.sailcbi.APIServer.Entities.cacheable.sunset.{SunsetCache, SunsetCacheKey}
import org.sailcbi.APIServer.Entities.cacheable.yearlydate.{YearlyDateAndItemCache, YearlyDateAndItemCacheKey, YearlyDateItemCache, YearlyDateItemCacheKey}
import org.sailcbi.APIServer.Entities.dto.GuidedSailTimeSlotRangeDTO
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.MemberRequestCache
import play.api.libs.json.{JsValue, Json}

import java.io.{ByteArrayOutputStream, PrintWriter}
import java.time.format.DateTimeFormatter
import java.time.{DayOfWeek, Duration, LocalDate, LocalDateTime, LocalTime}
import scala.sys.process.{ProcessLogger, stringSeqToProcess}

case class SunsetAPIItem(date: String, sunrise: String, sunset: String, first_light: String, last_light: String, dawn: String, dusk: String, solar_noon: String, golden_hour: String, day_length: String, timezone: String, utc_offset: Int) {
}
object SunsetAPIItem {
	implicit val format = Json.format[SunsetAPIItem]
	def apply(v: JsValue): SunsetAPIItem = {
		v.as[SunsetAPIItem]
	}
}

case class SunsetAPIResults(results: Array[SunsetAPIItem]) {
}

object SunsetAPIResults {
	implicit val format = Json.format[SunsetAPIResults]
	def apply(v: JsValue): SunsetAPIResults = {
		v.as[SunsetAPIResults]
	}
}

case class SunsetTime(for_date: LocalDateTime, twilight_start: LocalDateTime, twilight_end: LocalDateTime, sunrise: LocalDateTime, sunset: LocalDateTime, sonar_noon: LocalDateTime, nautical_twilight_start: LocalDateTime, nautical_twilight_end: LocalDateTime, astronomical_twilight_start: LocalDateTime, astronomical_twilight_end: LocalDateTime, day_length_seconds: Int){
}
object SunsetStuff {
	private val formatter = DateTimeFormatter.ofPattern("h:mm:ss a")
	def mapSunsetItem(item: SunsetAPIItem): SunsetTime = {
		println("trying")
		val date = LocalDate.parse(item.date)
		println("Dateer", date)
		val sunrise = LocalTime.parse(item.sunrise, formatter)
		val sunset = LocalTime.parse(item.sunset, formatter)
		val last_light = LocalTime.parse(item.last_light, formatter)
		val dusk = LocalTime.parse(item.dusk, formatter)
		val dawn = LocalTime.parse(item.dawn, formatter)
		val solar_noon = LocalTime.parse(item.solar_noon, formatter)
		val twilight_start = date.atTime(dusk)
		val twilight_end = date.atTime(last_light)
		val day_length_seconds = Duration.between(dawn, dusk).toSeconds.intValue
		SunsetTime(date.atTime(LocalTime.MIDNIGHT), twilight_start, twilight_end, date.atTime(sunrise), date.atTime(sunset), date.atTime(solar_noon), twilight_start, twilight_end, twilight_start, twilight_end, day_length_seconds)
	}
	def mapToSQL(time: SunsetTime): String = {
		s"""insert into sunset_times (for_date, sunrise, sunset, sonar_noon, twilight_start, twilight_end, nautical_twilight_start, nautical_twilight_end, astronomical_twilight_start, astronomical_twilight_end, day_length_seconds) values (${DateUtil.toDateSQL(time.for_date)},${DateUtil.toDateSQL(time.sunrise)},${DateUtil.toDateSQL(time.sunset)},${DateUtil.toDateSQL(time.sonar_noon)},${DateUtil.toDateSQL(time.twilight_start)},${DateUtil.toDateSQL(time.twilight_end)},${DateUtil.toDateSQL(time.nautical_twilight_start)},${DateUtil.toDateSQL(time.nautical_twilight_end)},${DateUtil.toDateSQL(time.astronomical_twilight_start)},${DateUtil.toDateSQL(time.astronomical_twilight_end)},${time.day_length_seconds})"""
	}
}

object ApClassLogic {
	val AP_GUIDED_SAIL_DURATION = Duration.ofMinutes(90)
	val AP_GUIDED_SAIL_INCREMENT = Duration.ofMinutes(15)
	val DATETIME_RANGE_TYPE = "guided-sail-blackout"

	/*Returns a list of start and end times for guided sailing time slots on a specific day
	use above parameters to change duration and time between these slots
	will ignore times
	* */

	val ONE_DAY = Duration.ofDays(1)

	def getApGuidedSailTimeSlots(rc: RequestCache, forMonth: LocalDate): List[GuidedSailTimeSlotRangeDTO] = {
		println("ForMonth", forMonth)
		val forMonthStart = forMonth.withDayOfMonth(1)
		val forMonthEnd = forMonth.plusMonths(1).minusDays(1)
		val blackoutDateRanges = getBlackoutRanges(rc, forMonthStart, forMonthEnd)
		val sunsetsCached = SunsetCache.get(rc, SunsetCacheKey(forMonth.getYear, forMonth.getMonthValue, Option.empty))
		//println("Sunset Cached" + sunsetsCached)
		val sunsetTimes = sunsetsCached._1.map(sunset => sunset.values.sunset.get)
		//YearlyDateAndItemCache.nuke(rc, YearlyDateAndItemCacheKey(2020, "JP_CLASS_SCHEDULE"))
		val startAndSunsetTimes = sunsetTimes.map(a => (a, getStartTime(a.toLocalDate)))
		startAndSunsetTimes.map(times => findStartAndEndTime(createDateRangesWithBlackouts(times._1.toLocalDate.atTime(times._2), times._1, AP_GUIDED_SAIL_DURATION, AP_GUIDED_SAIL_INCREMENT, blackoutDateRanges), AP_GUIDED_SAIL_DURATION))
		//val times = sunsetTimes.map(sunset => createDateRangesWithBlackouts(sunset.withHour(getStartTime(sunset.toLocalDate)), sunset, AP_GUIDED_SAIL_DURATION, AP_GUIDED_SAIL_INCREMENT, blackoutRanges))
	}

	private def getBlackoutRanges(rc: RequestCache, startDate: LocalDate, endDate: LocalDate): List[(LocalDateTime, LocalDateTime)] = {
		val blackoutDateRanges = DatetimeRangeCache.get(rc, DatetimeRangeCacheKey(startDate, endDate, DATETIME_RANGE_TYPE))
		blackoutDateRanges._1.map(range => (range.values.startDatetime.get, range.values.endDatetime.get))
	}

	private def findStartAndEndTime(slots: List[LocalDateTime], slotDuration: Duration): GuidedSailTimeSlotRangeDTO = {
		GuidedSailTimeSlotRangeDTO(slots.head.format(DateUtil.DATE_TIME_FORMATTER), slots.last.plus(slotDuration).format(DateUtil.DATE_TIME_FORMATTER))
	}

	def getApGuidedSailTimeSlotsForDay(rc: RequestCache, forDate: LocalDate): List[(LocalTime, LocalTime)] = {
		if (!programOpen(forDate)) List.empty
		else {
			SunsetCache.nuke(rc, SunsetCacheKey(forDate.getYear, forDate.getMonthValue, Option(forDate.getDayOfMonth)))
			val sunsetTime = SunsetCache.get(rc, SunsetCacheKey(forDate.getYear, forDate.getMonthValue, Option(forDate.getDayOfMonth)))._1.headOption
			val startTime = getStartTime(forDate)
			println("For Date", forDate)
			println("Sunset Time", sunsetTime)
			println("Start Time", startTime)
			val blackoutRanges = getBlackoutRanges(rc, forDate, forDate)
			println("Blackout Ranges", blackoutRanges)
			sunsetTime.map(sunset => createDateRangesWithBlackouts(
				forDate.atTime(startTime),
				sunset.values.sunset.get,
				AP_GUIDED_SAIL_DURATION,
				AP_GUIDED_SAIL_INCREMENT,
				blackoutRanges
			)).getOrElse(List.empty).map(startTime => (startTime.toLocalTime, startTime.toLocalTime.plus(AP_GUIDED_SAIL_DURATION)))
		}
	}

	def getApGuidedSailSessionsCurrent(rc: RequestCache, forMonth: String): Unit = {
		//TODO get current guided sail class sessions
		/*val q = QueryBuilder.from(ApClassSession).where(List(
			ApClassSession.fields.
		))*/
	}

	def apGuidedSailRegisterForSession(rc: RequestCache, timeslot: String): Unit = {
		//TODO
	}

	def apGuidedSailCancelSession(rc: RequestCache, sessionId: Int): Unit = {
		//TODO
	}
/*
	def updateSunsetTimesFromAPI(rc: RequestCache): Unit = {
		val stdoutStream = new ByteArrayOutputStream
		val stderrStream = new ByteArrayOutputStream
		val stdoutWriter = new PrintWriter(stdoutStream)
		val stderrWriter = new PrintWriter(stderrStream)
		val cmd: Seq[String] = Seq("/usr/bin/curl", "-s", "https://api.sunrisesunset.io/json?lat=42.36056983460875&lng=-71.07341215992227&timezone=UTC-5&date_start=2024-01-01&date_end=2024-12-31")
		val exitValue = cmd.!(ProcessLogger(stdoutWriter.println, stderrWriter.println))
		stdoutWriter.close()
		stderrWriter.close()
		println("Items", exitValue)
		println("Output", stdoutStream.toString)
		println("Errors", stderrStream.toString)
		val dateNode = Json.parse(stdoutStream.toString)
		val dates = SunsetAPIResults.apply(dateNode)
		val sunsetTimes = dates.results.map(SunsetStuff.mapSunsetItem)
		sunsetTimes.foreach(sunsetTime => {
			val q = new PreparedQueryForInsert(Set(MemberRequestCache)) {
				override val pkName: Option[String] = Some("ROW_ID")

				override def getQuery: String = SunsetStuff.mapToSQL(sunsetTime)
			}
			rc.executePreparedQueryForInsert(q)
		})
	}
*/
	private def getStartTime(forDate: LocalDate): LocalTime = if (forDate.getDayOfWeek.getValue > DayOfWeek.FRIDAY.getValue) LocalTime.of(9, 0, 0) else LocalTime.of(13, 0, 0)

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
