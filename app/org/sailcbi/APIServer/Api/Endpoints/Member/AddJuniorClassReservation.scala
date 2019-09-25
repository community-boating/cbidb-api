package org.sailcbi.APIServer.Api.Endpoints.Member

import java.time.LocalDateTime

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Junior.JPPortal
import org.sailcbi.APIServer.IO.PreparedQueries.{PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import org.sailcbi.APIServer.Services.Authentication.{MemberUserType, ProtoPersonUserType}
import org.sailcbi.APIServer.Services.PermissionsAuthority.UnauthorizedAccessException
import org.sailcbi.APIServer.Services.{CacheBroker, PermissionsAuthority, PersistenceBroker, RequestCache}
import play.api.libs.json.JsValue
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

class AddJuniorClassReservation @Inject()(implicit exec: ExecutionContext) extends Controller {
	def post()(implicit PA: PermissionsAuthority) = Action { request =>
		try {
			val logger = PA.logger
			val parsedRequest = ParsedRequest(request)
			val rc: RequestCache = PA.getRequestCache(ProtoPersonUserType, None, parsedRequest)._2.get
			val data = request.body.asJson
			data match {
				case None => {
					println("no body")
					new Status(400)("no body")
				}
				case Some(v: JsValue) => {
					println(v)
					val parsed = AddJuniorClassReservationShape.apply(v)

					doPost(rc, parsedRequest, parsed)
					Ok("done")
				}
				case Some(v) => {
					println("wut dis " + v)
					Ok("wat")
				}
			}
		} catch {
			case _: UnauthorizedAccessException => Ok("Access Denied")
			case e: Throwable => {
				println(e)
				e.printStackTrace()
				Ok("Internal Error")
			}
		}
	}

	// TODO: rollbacks

	private def doPost(rc: RequestCache, pr: ParsedRequest, body: AddJuniorClassReservationShape): Unit = {

		val pb: PersistenceBroker = rc.pb
		val cookieValue = pr.cookies(ProtoPersonUserType.COOKIE_NAME).value

		// Create protoparent if it doenst exist
		val protoParentExists = ProtoPersonUserType.getAuthedPersonId(cookieValue, pb).isDefined
		val parentPersonId = {
			if (protoParentExists) {
				val ret = ProtoPersonUserType.getAuthedPersonId(cookieValue, pb).get
				println("reusing existing protoparent record for this cookie val " + ret)
				ret
			} else {
				val ret = JPPortal.persistProtoParent(pb, cookieValue)
				println("created new protoparent: " + ret)
				ret
			}
		}

		// Create new protojunior
		val juniorPersonId = JPPortal.persistProtoJunior(pb, parentPersonId, body.juniorFirstName)

		// create new signup with the min(signup_time) of all this protoparent's other signups
		val minSignupTime = JPPortal.getMinSignupTimeForParent(pb, parentPersonId)
		println("min signup is " + minSignupTime)

		// TODO: make a JPPortal.startnewJuniorTransation() that returns the personID as well as the rollback stuff scoped to that personid
		val rollback = () => {
			val deletePersonRelationship = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonUserType)) {
				override val params: List[String] = List(juniorPersonId.toString)

				override def getQuery: String =
					"""
					  |delete from person_relationships where b = ?
					  |""".stripMargin
			}
			val deletePerson = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonUserType)) {
				override val params: List[String] = List(juniorPersonId.toString)

				override def getQuery: String =
					"""
					  |delete from persons where person_id = ?
					  |""".stripMargin
			}
		}
	}
}
