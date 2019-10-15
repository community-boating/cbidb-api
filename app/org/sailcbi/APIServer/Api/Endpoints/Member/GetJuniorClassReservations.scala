package org.sailcbi.APIServer.Api.Endpoints.Member

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Junior.JPPortal
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Logic.JuniorProgramLogic
import org.sailcbi.APIServer.Services.Authentication.ProtoPersonUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority.UnauthorizedAccessException
import org.sailcbi.APIServer.Services.{PermissionsAuthority, RequestCache, ResultSetWrapper}
import play.api.libs.json.{JsValue, Json}
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

			val q = new PreparedQueryForSelect[ClassInfo](Set(ProtoPersonUserType)) {
				override val params: List[String] = List(rc.auth.userName)

				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): ClassInfo = {
					val signupDatetime = rsw.getLocalDateTime(3)
					val expDatetime = rsw.getLocalDateTime(3).plusMinutes(JuniorProgramLogic.SIGNUP_RESERVATION_HOLD_MINUTES)
					val currentTime = rsw.getLocalDateTime(4)
					val minutesRemaining = if (expDatetime.isBefore(currentTime)) -1L else currentTime.until(expDatetime, ChronoUnit.MINUTES)
					ClassInfo(
						juniorFirstName =  rsw.getString(1),
						instanceId = rsw.getInt(2),
						signupDatetime = signupDatetime,
						expirationDateTime = expDatetime,
						minutesRemaining = minutesRemaining
					)
				}

				override def getQuery: String =
					"""
					  |select k.name_first, si.instance_id, si.signup_datetime, util_pkg.get_sysdate
					  |from persons par, person_relationships rl, persons k, jp_class_signups si
					  |where par.person_id = rl.a and rl.b = k.person_id and k.person_id = si.person_id
					  |and par.PROTOPERSON_COOKIE = ?
					  |""".stripMargin
			}
			implicit val classInfoFormat = ClassInfo.format
			val results: JsValue = Json.toJson(pb.executePreparedQueryForSelect(q).map(o => Json.toJson(o)))
			Ok(results)
		} catch {
			case _: UnauthorizedAccessException => Ok("Access Denied")
			case e: Throwable => {
				println(e)
				e.printStackTrace()
				Ok("Internal Error")
			}
		}
	}

	case class ClassInfo(juniorFirstName: String, instanceId: Int, signupDatetime: LocalDateTime, expirationDateTime: LocalDateTime, minutesRemaining: Long)
	object ClassInfo {
		implicit val format = Json.format[ClassInfo]
		def apply(v: JsValue): ClassInfo = v.as[ClassInfo]
	}
}
