package org.sailcbi.APIServer.Api.Endpoints.Member

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Junior.JPPortal
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.ProtoPersonUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority.UnauthorizedAccessException
import org.sailcbi.APIServer.Services.{PermissionsAuthority, RequestCache, ResultSetWrapper}
import play.api.libs.json.{JsArray, JsObject, JsString, JsValue, Json}
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

class GetJuniorClassReservations @Inject()(implicit exec: ExecutionContext) extends Controller {
	def get()(implicit PA: PermissionsAuthority) = Action { request =>
		try {
			val logger = PA.logger
			val parsedRequest = ParsedRequest(request)
			val rc: RequestCache = PA.getRequestCache(ProtoPersonUserType, None, parsedRequest)._2.get
			PA.sleep()

			val pb = rc.pb

			val deleted = JPPortal.pruneOldReservations(pb)
			println(s"deleted $deleted old reservations...")

			val signupsQ = new PreparedQueryForSelect[ClassInfo](Set(ProtoPersonUserType)) {
				override val params: List[String] = List(rc.auth.userName)

				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): ClassInfo = {
					val signupDatetime = rsw.getLocalDateTime(4)
					val expDatetime = rsw.getLocalDateTime(5)
					val currentTime = rsw.getLocalDateTime(6)
					val minutesRemaining = if (expDatetime.isBefore(currentTime)) -1L else currentTime.until(expDatetime, ChronoUnit.MINUTES)
					ClassInfo(
						juniorPersonId = rsw.getInt(1),
						juniorFirstName =  rsw.getString(2),
						instanceId = rsw.getInt(3),
						signupDatetime = signupDatetime,
						expirationDateTime = expDatetime,
						minutesRemaining = minutesRemaining,
						signupNote = rsw.getOptionString(7)
					)
				}

				override def getQuery: String =
					"""
					  |select k.person_id, k.name_first, si.instance_id, si.signup_datetime,
					  |si.signup_datetime + (jp_class_pkg.get_minutes_to_reserve_class / (24 * 60)) as exp_datetime,
					  |util_pkg.get_sysdate,
					  |si.parent_signup_note
					  |from persons par, person_relationships rl, persons k, jp_class_signups si
					  |where par.person_id = rl.a and rl.b = k.person_id and k.person_id = si.person_id and si.signup_type = 'P'
					  |and k.proto_state = 'I'
					  |and par.PROTOPERSON_COOKIE = ?
					  |""".stripMargin
			}
			val signups = pb.executePreparedQueryForSelect(signupsQ)

			val noSignupJuniors = new PreparedQueryForSelect[String](Set(ProtoPersonUserType)) {
				override val params: List[String] = List(rc.auth.userName)

				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): String = rsw.getString(1)

				override def getQuery: String =
					"""
					  |select k.name_first
					  |from persons par
					  |inner join person_relationships rl
					  | on par.person_id = rl.a
					  | inner join persons k
					  |  on k.person_id = rl.b
					  |  left outer join jp_class_signups si
					  |  on k.person_id = si.person_id and si.signup_type = 'P'
					  |where k.proto_state = 'I'
					  |and par.PROTOPERSON_COOKIE = ?
					  |and si.signup_id is null
					  |
					  |""".stripMargin
			}

			implicit val classInfoFormat = ClassInfo.format
			val results: JsValue = Json.toJson(signups.map(o => Json.toJson(o)))
			val noSignups: JsArray = JsArray(pb.executePreparedQueryForSelect(noSignupJuniors).toArray.map(JsString))
			Ok(JsObject(Map(
				"instances" -> results,
				"noSignups" -> noSignups
			)))
		} catch {
			case _: UnauthorizedAccessException => Ok("Access Denied")
			case e: Throwable => {
				println(e)
				e.printStackTrace()
				Ok("Internal Error")
			}
		}
	}

	case class ClassInfo(
		juniorPersonId: Int,
		juniorFirstName: String,
		instanceId: Int,
		signupDatetime: LocalDateTime,
		expirationDateTime: LocalDateTime,
		minutesRemaining: Long,
		signupNote: Option[String]
	)

	object ClassInfo {
		implicit val format = Json.format[ClassInfo]
		def apply(v: JsValue): ClassInfo = v.as[ClassInfo]
	}
}
