package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.Api.{ValidationError, ValidationOk, ValidationResult}
import org.sailcbi.APIServer.CbiUtil.{ParsedRequest, PhoneUtil}
import org.sailcbi.APIServer.IO.PreparedQueries.{PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.Exception.UnauthorizedAccessException
import org.sailcbi.APIServer.Services._
import play.api.libs.json.{JsNumber, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext

class EmergencyContact @Inject()(implicit exec: ExecutionContext) extends Controller {
	def get(juniorId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action { request =>
		val parsedRequest = ParsedRequest(request)
		val rc: RequestCache = PA.getRequestCacheMemberWithJuniorId(None, parsedRequest, juniorId).get
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
			val rc: RequestCache = PA.getRequestCacheMemberWithJuniorId(None, parsedRequest, juniorId).get
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

					runValidations(parsed, pb, None) match {
						case ve: ValidationError => Ok(ve.toResultError.asJsObject())
						case ValidationOk => {
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

							Ok(new JsObject(Map(
								"personId" -> JsNumber(parsed.personId)
							)))
						}
					}

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

	def runValidations(parsed: EmergencyContactShape, pb: PersistenceBroker, juniorId: Option[Int]): ValidationResult = {
		val unconditionalValidations = List(
			ValidationResult.checkBlank(parsed.emerg1Name, "First Contact Name"),
			ValidationResult.checkBlank(parsed.emerg1Relation, "First Contact Relation"),
			ValidationResult.checkBlank(parsed.emerg1PhonePrimary, "First Contact Primary Phone"),
			ValidationResult.inline(parsed.emerg1PhonePrimary)(
				phone => PhoneUtil.regex.findFirstIn(phone.getOrElse("")).isDefined,
				"First Contact Primary Phone is not valid."
			),
			ValidationResult.checkBlank(parsed.emerg1PhonePrimaryType, "First Contact Primary Phone Type"),
		)

		val conditionalValidations = List((
			parsed.emerg1PhoneAlternate.isDefined,
			ValidationResult.inline(parsed.emerg1PhoneAlternate)(
				phone => PhoneUtil.regex.findFirstIn(phone.getOrElse("")).isDefined,
				"First Contact Alternate Phone is not valid."
			)
		), (
			parsed.emerg2PhonePrimary.isDefined,
			ValidationResult.inline(parsed.emerg2PhonePrimary)(
				phone => PhoneUtil.regex.findFirstIn(phone.getOrElse("")).isDefined,
				"Second Contact Primary Phone is not valid."
			)
		), (
			parsed.emerg2PhoneAlternate.isDefined,
			ValidationResult.inline(parsed.emerg2PhoneAlternate)(
				phone => PhoneUtil.regex.findFirstIn(phone.getOrElse("")).isDefined,
				"Second Contact Alternate Phone is not valid."
			)
		)).filter(_._1).map(_._2)

		ValidationResult.combine(unconditionalValidations ::: conditionalValidations)
	}
}