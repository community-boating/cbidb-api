package org.sailcbi.APIServer.Logic

import com.coleji.neptune.Core.RequestCache
import com.coleji.neptune.IO.PreparedQueries.{PreparedProcedureCall, PreparedQueryForUpdateOrDelete, PreparedValue}
import com.coleji.neptune.Storable.StorableQuery.QueryBuilder
import com.coleji.neptune.Storable.StorableQuery.TableAlias.apply
import com.coleji.neptune.Util.DateUtil
import org.sailcbi.APIServer.Entities.EntityDefinitions.{ApClassInstance, ApClassSession, ApClassSignup, YearlyDate}
import org.sailcbi.APIServer.Entities.cacheable.DatetimeRange.{DatetimeRangeCache, DatetimeRangeCacheKey}
import org.sailcbi.APIServer.Entities.cacheable.sunset.{SunsetCache, SunsetCacheKey}
import org.sailcbi.APIServer.Entities.cacheable.yearlydate.{YearlyDateAndItemCache, YearlyDateAndItemCacheKey}
import org.sailcbi.APIServer.Entities.dto.{GuidedSailCurrentSessionDTO, GuidedSailTimeSlotsForDayDTO}
import org.sailcbi.APIServer.UserTypes.MemberRequestCache

import java.sql.CallableStatement
import java.time.{DayOfWeek, Duration, LocalDate, LocalDateTime, LocalTime}

object ApClassLogic {
	private val AP_GUIDED_SAIL_DURATION = Duration.ofMinutes(90)
	private val AP_GUIDED_SAIL_INCREMENT = Duration.ofMinutes(15)
	private val DATETIME_RANGE_TYPE = "guided-sail-blackout"
	private val JP_SEASON_YEARLY_ITEM_ALIAS = "JP_CLASS_SCHEDULE"

	/*Returns a list of guided sailing time slots for a specified month
	* */

	def getApGuidedSailTimeSlots(rc: RequestCache, forMonth: LocalDate): List[GuidedSailTimeSlotsForDayDTO] = {
		val forMonthStart = forMonth.withDayOfMonth(1)
		val forMonthEnd = forMonth.plusMonths(1).minusDays(1)
		val jpClassSchedule = YearlyDateAndItemCache.get(rc, YearlyDateAndItemCacheKey(forMonth.getYear, JP_SEASON_YEARLY_ITEM_ALIAS))._1.headOption.map(a => a._2)
		val blackoutDateRanges = getBlackoutRanges(rc, forMonthStart, forMonthEnd)
		val sunsetsCached = SunsetCache.get(rc, SunsetCacheKey(forMonth.getYear, forMonth.getMonthValue, Option.empty))
		val sunsetTimes = sunsetsCached._1.map(sunset => sunset.values.sunset.get).filter(p => programOpen(p.toLocalDate))
		val startAndSunsetTimes = sunsetTimes.map(a => (a, getStartTime(a.toLocalDate, jpClassSchedule))).filter(a => a._1.isAfter(LocalDateTime.now()))
		startAndSunsetTimes.map(times => toDTO(createDateRangesWithBlackouts(times._1.toLocalDate.atTime(times._2), times._1, AP_GUIDED_SAIL_DURATION, AP_GUIDED_SAIL_INCREMENT, blackoutDateRanges).filter(a => a.isAfter(LocalDateTime.now()))))
	}

	private def getBlackoutRanges(rc: RequestCache, startDate: LocalDate, endDate: LocalDate): List[(LocalDateTime, LocalDateTime)] = {
		val blackoutDateRanges = DatetimeRangeCache.get(rc, DatetimeRangeCacheKey(startDate, endDate, DATETIME_RANGE_TYPE))
		blackoutDateRanges._1.map(range => (range.values.startDatetime.get, range.values.endDatetime.get))
	}

	private def toDTO(slots: List[LocalDateTime]): GuidedSailTimeSlotsForDayDTO = {
		GuidedSailTimeSlotsForDayDTO(slots.headOption.getOrElse(LocalDateTime.now()).format(DateUtil.DATE_FORMATTER), slots.map(slot => (slot.format(DateUtil.TIME_FORMATTER), slot.plus(AP_GUIDED_SAIL_DURATION).format(DateUtil.TIME_FORMATTER))))
	}

	/*
	Create new class instance and session of a given format and session date time
	 */

	private def createClassInstance(rc: RequestCache, formatId: Int, sessionDatetime: LocalDateTime): Int = {

		val proc = new PreparedProcedureCall[Int](Set(MemberRequestCache)) {

			override def setInParametersInt: Map[String, Int] = Map(
				"p_format_id" -> formatId,
			)

			override def setInParametersVarchar: Map[String, String] = Map(
				"p_session_datetime" -> sessionDatetime.format(DateUtil.DATE_TIME_FORMATTER),
			)

			override def getQuery: String = "ap_class_pkg.create_class_instance_vc(?, ?, ?)"

			override def registerOutParameters: Map[String, Int] = Map(
				"p_instance_id" -> java.sql.Types.INTEGER
			)

			override def getOutResults(cs: CallableStatement): Int = cs.getInt("p_instance_id")
		}
		rc.executeProcedure(proc)
	}

	/*
	Set instructor id of a class instance
	 */

	private def setClassInstructor(rc: RequestCache, instanceId: Int, personId: Int): String = {
		val proc = new PreparedProcedureCall[String](Set(MemberRequestCache)) {

			override def setInParametersInt: Map[String, Int] = Map(
				"i_person_id" -> personId,
				"i_instance_id" -> instanceId
			)

			override def setInParametersVarchar: Map[String, String] = Map(
				"i_override" -> "N"
			)

			override def getQuery: String = "ap_class_pkg.attempt_set_ap_class_instructor(?, ?, ?, ?)"

			override def registerOutParameters: Map[String, Int] = Map(
				"o_error" -> java.sql.Types.VARCHAR
			)

			override def getOutResults(cs: CallableStatement): String = cs.getString("o_error")
		}
		rc.executeProcedure(proc)
	}

	/*
	Unenroll instructor from current class
	 */

	private def unenrollClassInstructor(rc: RequestCache, instanceId: Int, personId: Int): String = {
		val proc = new PreparedProcedureCall[String](Set(MemberRequestCache)) {

			override def setInParametersInt: Map[String, Int] = Map(
				"i_person_id" -> personId,
				"i_instance_id" -> instanceId
			)

			override def getQuery: String = "ap_class_pkg.attempt_unenroll_ap_class_instructor(?, ?, ?)"

			override def registerOutParameters: Map[String, Int] = Map(
				"o_error" -> java.sql.Types.VARCHAR
			)

			override def getOutResults(cs: CallableStatement): String = cs.getString("o_error")
		}
		rc.executeProcedure(proc)
	}

	/*
	Cancel class instance and session and also make sure instructor_id is empty so the
	class time conflict doesnt think the person is still teaching this cancelled class
	 */

	private def cancelClassInstance(rc: RequestCache, instanceId: Int): Unit = {
		val proc = new PreparedProcedureCall[Unit](Set(MemberRequestCache)) {

			override def setInParametersInt: Map[String, Int] = Map(
				"p_instance_id" -> instanceId
			)

			override def getQuery: String = "ap_class_pkg.cancel_instance(?)"

			override def registerOutParameters: Map[String, Int] = Map.empty

			override def getOutResults(cs: CallableStatement): Unit = ()
		}
		rc.executeProcedure(proc)
		val queryUpdateInstructorId = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
			override def getQuery: String = "update ap_class_instances set instructor_id = null where instance_id = ?"

			override def getParams: List[PreparedValue] = List(instanceId)
		}
		rc.executePreparedQueryForUpdateOrDelete(queryUpdateInstructorId)
	}

	/*
	Update value of cancelled_datetime to a specified value and instance id
	 */

	private def updateApClassCancelled(rc: RequestCache, tableName: String, datetimeValue: String, instanceId: Int): Unit = {
		val queryRemoveCancelDatetimeInstance = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
			override def getQuery: String = s"update $tableName set cancelled_datetime = $datetimeValue where instance_id = ?"

			override def getParams: List[PreparedValue] = List(instanceId)
		}
		rc.executePreparedQueryForUpdateOrDelete(queryRemoveCancelDatetimeInstance)
	}

	/*
	Returns a list of current instances and sessions along with the current number of signups
	that the given person is currently the instructor (hides cancelled instances)
	 */

	def getApGuidedSailInstancesCurrent(rc: RequestCache, personId: Int): List[GuidedSailCurrentSessionDTO] = {
		val qbSessions = QueryBuilder.from(ApClassSession)
			.innerJoin(ApClassInstance, ApClassSession.fields.instanceId.alias.equalsField(ApClassInstance.fields.instanceId.alias))
			.where(List(
				ApClassInstance.fields.formatId.alias.equalsConstant(GUIDED_SAIL_CLASS_FORMAT),
				ApClassInstance.fields.instructorId.alias.equalsConstant(Some(personId)),
				ApClassSession.fields.cancelledDatetime.alias.isNull
			)).select(List(
				ApClassSession.fields.sessionDatetime.alias,
				ApClassSession.fields.sessionLength.alias,
				ApClassSession.fields.instanceId.alias,
				ApClassInstance.fields.instanceId.alias,
				ApClassInstance.fields.instructorId.alias,
				ApClassInstance.fields.signupMax.alias
			))
		val sessionsAndInstances = rc.executeQueryBuilder(qbSessions).map(raw => (ApClassSession.construct(raw), ApClassInstance.construct(raw)))
		val qb = QueryBuilder.from(ApClassSignup)
			.where(List(
				ApClassSignup.fields.instanceId.alias.inList(sessionsAndInstances.map(a => a._1.values.instanceId.get)),
				ApClassSignup.fields.signupType.alias.equalsConstant("E")
			)).select(List(
				ApClassSignup.fields.instanceId.alias
			))
		val signups = rc.executeQueryBuilder(qb).map(ApClassSignup.construct)
		sessionsAndInstances.map(sessionAndInstance => GuidedSailCurrentSessionDTO(sessionAndInstance._1.values.sessionDatetime.get.format(DateUtil.DATE_TIME_FORMATTER),
			sessionAndInstance._1.values.sessionLength.get,
			sessionAndInstance._1.values.instanceId.get,
			signups.count(signup => signup.values.instanceId.get == sessionAndInstance._1.values.instanceId.get),
			sessionAndInstance._2.values.signupMax.get.getOrElse(1)
		))
	}

	private val GUIDED_SAIL_CLASS_FORMAT = 1101

	/*
	Get current AP class instances returns instance id if successful or error if failure
	 */

	def apGuidedSailCreateInstance(rc: RequestCache, timeslot: LocalDateTime, personId: Int): (Option[(Int, Int)], Option[String]) = {
		val currentSessionsQB = QueryBuilder.from(ApClassSession).innerJoin(ApClassInstance,
				ApClassSession.fields.instanceId.alias.equalsField(ApClassInstance.fields.instanceId.alias)
			).where(List(
				ApClassSession.fields.sessionDatetime.alias.isDateConstant(timeslot.toLocalDate),
				ApClassInstance.fields.instructorId.alias.isNull,
				ApClassInstance.fields.formatId.alias.equalsConstant(GUIDED_SAIL_CLASS_FORMAT)
			))
			.select(List(
				ApClassSession.fields.sessionDatetime,
				ApClassSession.fields.cancelledDatetime,
				ApClassSession.fields.instanceId,
				ApClassSession.fields.sessionId
			))
		val currentSession = rc.executeQueryBuilder(currentSessionsQB).map(ApClassSession.construct).find(a => a.values.sessionDatetime.get.isEqual(timeslot))
		val currentInstanceId = currentSession match {
			case Some(session) => session.values.instanceId.get
			case None => createClassInstance(rc, GUIDED_SAIL_CLASS_FORMAT, timeslot)
		}
		//If there's a session at the timeslot with this person's Id then we will reuse it and just un cancel it
		if(currentSession.isDefined && currentSession.get.values.cancelledDatetime.get.isDefined){
			updateApClassCancelled(rc, "ap_class_instances", "null", currentInstanceId)
			updateApClassCancelled(rc, "ap_class_sessions", "null", currentInstanceId)
		}
		val errors = setClassInstructor(rc, currentInstanceId, personId)
		val hasErrors = !(errors == null || errors.isEmpty)
		if(hasErrors){
			updateApClassCancelled(rc, "ap_class_instances", "sysdate", currentInstanceId)
			updateApClassCancelled(rc, "ap_class_sessions", "sysdate", currentInstanceId)
		}
		if(hasErrors)
			(None, Some(errors))
		else
			(Some((currentInstanceId, getCurrentSignups(rc, currentInstanceId))), None)
	}

	private def getCurrentSignups(rc: RequestCache, instanceId: Int): Int = {
		val currentSignupsQB = QueryBuilder.from(ApClassSignup).where(List(
			ApClassSignup.fields.instanceId.alias.equalsConstant(instanceId),
			ApClassSignup.fields.signupType.alias.equalsConstant("E")
		)).select(List(
			ApClassSignup.fields.instanceId.alias
		))
		rc.executeQueryBuilder(currentSignupsQB).size
	}

	/*
	Cancel a guided sail class instance, if there are any signups the
	unenroll instructor logic is called, otherwise the class can just be marked as
	cancelled.
	 */

	def apGuidedSailCancelInstance(rc: RequestCache, instanceId: Int, personId: Int): Option[String] = {
		val count = getCurrentSignups(rc, instanceId)
		if(count > 0){
			val error = unenrollClassInstructor(rc, instanceId, personId)
			if(error != null && error.nonEmpty)
				Some(error)
			else
				None
		}else{
			cancelClassInstance(rc, instanceId)
			None
		}
	}

	/*
	Returns the start time for a given day, depending on if it is summertime or the weekend.
	 */
	private def getStartTime(forDate: LocalDate, jpClassSchedule: Option[YearlyDate]): LocalTime = {
		if (forDate.getDayOfWeek.getValue > DayOfWeek.FRIDAY.getValue) {
			LocalTime.of(9, 0, 0) //Weekends
		}else if(jpClassSchedule.isDefined && rangesOverlap(tangentCounts = true, (forDate.atTime(LocalTime.MIN), forDate.atTime(LocalTime.MAX)), (jpClassSchedule.get.values.startDate.get, jpClassSchedule.get.values.endDate.get.getOrElse(LocalDateTime.now())))){
			LocalTime.of(15, 0, 0) //Summer Weekdays (JP Yearly Date)
		}else{
			LocalTime.of(13, 0, 0) //Spring/Fall Weekdays
		}
	}

	/*
	Creates a list of datetimes from start to end of a given duration and separate increment
	 */

	private def createDateRanges(start: LocalDateTime, end: LocalDateTime, duration: Duration, increment: Duration): List[LocalDateTime] = {
		val count = Duration.between(start, end.minus(duration)).dividedBy(increment).floor.intValue
		(0 to count).map(i => start.plus(increment.multipliedBy(i))).toList
	}

	/*
	creates a similar list of datetimes but excluding times that overlap with the specified blackout ranges
	 */

	private def createDateRangesWithBlackouts(
		start: LocalDateTime, end: LocalDateTime, duration: Duration, increment: Duration, blackouts: List[(LocalDateTime, LocalDateTime)]
	): List[LocalDateTime] = {
		if (end.toLocalDate != start.toLocalDate) List.empty
		else {
			val starts = createDateRanges(start, end, duration, increment)
			starts.map(s => (s, s.plus(duration))).filter(r => {
				!blackouts.exists(b => rangesOverlap(tangentCounts = false, r, b))
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

	/*
	stuff to populate sunset times which are needed to make the guided sail slots show up
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

/*
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
		val date = LocalDate.parse(item.date)
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
*/
