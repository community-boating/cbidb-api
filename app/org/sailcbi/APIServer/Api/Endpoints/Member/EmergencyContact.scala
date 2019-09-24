package org.sailcbi.APIServer.Api.Endpoints.Member

import java.sql.ResultSet

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.PreparedQueries.{PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority.UnauthorizedAccessException
import org.sailcbi.APIServer.Services.{CacheBroker, PermissionsAuthority, PersistenceBroker, RequestCache, ResultSetWrapper}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext

class EmergencyContact @Inject()(implicit exec: ExecutionContext) extends Controller {
	def get(juniorId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action { request =>
		val parsedRequest = ParsedRequest(request)
		val rc: RequestCache = PA.getRequestCacheMemberWithJuniorId(None, parsedRequest, juniorId)._2.get
		val pb: PersistenceBroker = rc.pb
		val cb: CacheBroker = rc.cb

		val select = new PreparedQueryForSelect[EmergencyContactShape](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): EmergencyContactShape =
				EmergencyContactShape(
					juniorId,
					rs.getOptionString(1),
					rs.getOptionString(2),
					rs.getOptionString(3),
					rs.getOptionString(4),
					rs.getOptionString(5),
					rs.getOptionString(6),
					rs.getOptionString(7),
					rs.getOptionString(8),
					rs.getOptionString(9),
					rs.getOptionString(10),
					rs.getOptionString(11),
					rs.getOptionString(12)
				)

			override def getQuery: String =
				s"""
				   |select
				   |EMERG1_NAME,
				   |EMERG1_RELATION,
				   |EMERG1_PHONE_PRIMARY,
				   |EMERG1_PHONE_PRIMARY_TYPE,
				   |EMERG1_PHONE_ALTERNATE,
				   |EMERG1_PHONE_ALTERNATE_TYPE,
				   |EMERG2_NAME,
				   |EMERG2_RELATION,
				   |EMERG2_PHONE_PRIMARY,
				   |EMERG2_PHONE_PRIMARY_TYPE,
				   |EMERG2_PHONE_ALTERNATE,
				   |EMERG2_PHONE_ALTERNATE_TYPE
				   |from persons where person_id = ?
        """.stripMargin

			override val params: List[String] = List(juniorId.toString)
		}

		val resultObj = pb.executePreparedQueryForSelect(select).head
		val resultJson: JsValue = Json.toJson(resultObj)
		Ok(resultJson)
	}

	def post()(implicit PA: PermissionsAuthority) = Action { request =>
		try {
			val parsedRequest = ParsedRequest(request)
			val juniorId = request.body.asJson.map(json => json("personId").toString().toInt).get
			val rc: RequestCache = PA.getRequestCacheMemberWithJuniorId(None, parsedRequest, juniorId)._2.get
			val pb: PersistenceBroker = rc.pb
			val cb: CacheBroker = rc.cb
			val data = request.body.asJson
			data match {
				case None => {
					println("no body")
					new Status(400)("no body")
				}
				case Some(v: JsValue) => {
					val parsed = EmergencyContactShape.apply(v)
					println(parsed)


					val updateQuery = new PreparedQueryForUpdateOrDelete(Set(MemberUserType)) {
						override def getQuery: String =
							s"""
							   |update persons set
							   |EMERG1_NAME = ?,
							   |EMERG1_RELATION = ?,
							   |EMERG1_PHONE_PRIMARY = ?,
							   |EMERG1_PHONE_PRIMARY_TYPE = ?,
							   |EMERG1_PHONE_ALTERNATE = ?,
							   |EMERG1_PHONE_ALTERNATE_TYPE = ?,
							   |
                 |EMERG2_NAME = ?,
							   |EMERG2_RELATION = ?,
							   |EMERG2_PHONE_PRIMARY = ?,
							   |EMERG2_PHONE_PRIMARY_TYPE = ?,
							   |EMERG2_PHONE_ALTERNATE = ?,
							   |EMERG2_PHONE_ALTERNATE_TYPE = ?
							   |where person_id = ?
              """.stripMargin

						override val params: List[String] = List(
							parsed.emerg1Name.orNull,
							parsed.emerg1Relation.orNull,
							parsed.emerg1PhonePrimary.orNull,
							parsed.emerg1PhonePrimaryType.orNull,
							parsed.emerg1PhoneAlternate.orNull,
							parsed.emerg1PhoneAlternateType.orNull,

							parsed.emerg2Name.orNull,
							parsed.emerg2Relation.orNull,
							parsed.emerg2PhonePrimary.orNull,
							parsed.emerg2PhonePrimaryType.orNull,
							parsed.emerg2PhoneAlternate.orNull,
							parsed.emerg2PhoneAlternateType.orNull,
							parsed.personId.toString
						)
					}

					pb.executePreparedQueryForUpdateOrDelete(updateQuery)

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
				Ok("Internal Error")
			}
		}
	}
}