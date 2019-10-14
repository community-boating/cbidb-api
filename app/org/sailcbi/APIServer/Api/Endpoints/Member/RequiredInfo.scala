package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.Api.{ResultError, ValidationError, ValidationOk, ValidationResult}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.PreparedQueries.{PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority.UnauthorizedAccessException
import org.sailcbi.APIServer.Services._
import play.api.libs.json.{JsNumber, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext

class RequiredInfo @Inject()(implicit exec: ExecutionContext) extends Controller {
	def get(juniorId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action { request =>
		val parsedRequest = ParsedRequest(request)
		val rc: RequestCache = PA.getRequestCacheMemberWithJuniorId(None, parsedRequest, juniorId)._2.get
		val pb: PersistenceBroker = rc.pb
		val cb: CacheBroker = rc.cb

		val select = new PreparedQueryForSelect[RequiredInfoShape](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): RequiredInfoShape =
				RequiredInfoShape(
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
					rs.getOptionString(12),
					rs.getOptionString(13),
					rs.getOptionString(14),
					rs.getOptionString(15),
					rs.getOptionString(16),
					rs.getOptionString(17),
					rs.getOptionString(18),
					rs.getOptionString(19)
				)

			override def getQuery: String =
				s"""
				   |select
				   |name_first,
				   |name_last,
				   |name_middle_initial,
				   |to_char(dob, 'MM/DD/YYYY'),
				   |email,
				   |addr_1,
				   |addr_2,
				   |addr_3,
				   |city,
				   |state,
				   |zip,
				   |country,
				   |phone_primary,
				   |phone_primary_type,
				   |phone_alternate,
				   |phone_alternate_type,
				   |allergies,
				   |medications,
				   |special_needs
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
			val juniorId: Int = request.body.asJson.map(json => json("personId").toString().toInt).get
			println("required info post: juniorId is " + juniorId)
			val rc: RequestCache = PA.getRequestCacheMemberWithJuniorId(None, parsedRequest, juniorId)._2.get
			val pb: PersistenceBroker = rc.pb
			val cb: CacheBroker = rc.cb
			val data = request.body.asJson
			val successResponse = new JsObject(Map(
				"personId" -> JsNumber(juniorId)
			))
			data match {
				case None => {
					println("no body")
					Ok(ResultError.UNKNOWN)
				}
				case Some(v: JsValue) => {
					val parsed = RequiredInfoShape.apply(v)
					println(parsed)

					val dob = parsed.dob.getOrElse("")

					val phoneRegex = "^[0-9]{10}(x[0-9]+)?$".r

					val unconditionalValidations = List(
						tooOld(pb, dob),
						tooYoung(pb, dob, juniorId),
						ValidationResult.checkBlank(parsed.firstName, "First Name"),
						ValidationResult.checkBlank(parsed.lastName, "Last Name"),
						ValidationResult.checkBlank(parsed.dob, "Date of Birth"),
						ValidationResult.checkBlank(parsed.addr1, "Address"),
						ValidationResult.checkBlank(parsed.city, "City"),
						ValidationResult.checkBlank(parsed.state, "State"),
						ValidationResult.checkBlank(parsed.zip, "Zip code"),
						ValidationResult.checkBlank(parsed.primaryPhone, "Primary phone number"),
						ValidationResult.checkBlank(parsed.primaryPhoneType, "Primary phone number type"),
						ValidationResult.inline(parsed.zip)(zip => "^[0-9]{5}(-[0-9]{4})?$".r.findFirstIn(zip.getOrElse("")).isDefined, "Zip code is an invalid format."),
						ValidationResult.inline(parsed.childEmail)(
							childEmail => "^[A-Za-z0-9._%-]+@[A-Za-z0-9._%-]+\\.[A-Za-z]{2,4}$".r.findFirstIn(childEmail.getOrElse("")).isDefined,
							"Child email is not valid."
						),
						ValidationResult.inline(parsed.primaryPhone)(
							phone => phoneRegex.findFirstIn(phone.getOrElse("")).isDefined,
							"Primary Phone is not valid."
						)
					)

					val conditionalValidations = List((
						parsed.alternatePhone.isDefined,
						ValidationResult.inline(parsed.alternatePhone)(
							phone => phoneRegex.findFirstIn(phone.getOrElse("")).isDefined,
							"Alternate Phone is not valid."
						)
					), (
						parsed.alternatePhone.isDefined,
						ValidationResult.checkBlankCustom(parsed.alternatePhoneType, "Alternate phone type must be specified if alternate phone number is provided."),
					)).filter(_._1).map(_._2)

					// Run all validations and combine into one
					val validation = ValidationResult.combine(unconditionalValidations ::: conditionalValidations)

					// TODO: add more validations, come up with some chained try/promise arch
					validation match {
						case ve: ValidationError => Ok(ve.toResultError.asJsObject())
						case ValidationOk => {
							doUpdate(pb, parsed)
							Ok(successResponse)
						}
					}
				}
				case Some(v) => {
					println("wut dis " + v)
					Ok(ResultError.UNKNOWN)
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
	def tooOld(pb: PersistenceBroker, dob: String): ValidationResult = {
		val notTooOld = pb.executePreparedQueryForSelect(new PreparedQueryForSelect[Boolean](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Boolean = rs.getString(1).equals("Y")

			override def getQuery: String =
				s"""
				   |select person_pkg.is_jp_max_age(to_date(?,'MM/DD/YYYY')) from dual
				   |""".stripMargin

			override val params: List[String] = List(dob)
		}).head
		if (notTooOld) {
			ValidationOk
		} else {
			ValidationResult.from("Prospective juniors must be 17 or younger and may not turn 18 before the program begins.")
		}
	}

	def tooYoung(pb: PersistenceBroker, dob: String, juniorId: Int): ValidationResult = {
		val notTooYoung = pb.executePreparedQueryForSelect(new PreparedQueryForSelect[Boolean](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Boolean = rs.getString(1).equals("Y")

			override def getQuery: String =
				s"""
				   |select 1 from dual
				   |   where person_pkg.is_jp_min_age(to_date(?,'MM/DD/YYYY')) = 'Y'
				   |   or exists (select 1 from persons where person_id = ? and ignore_jp_min_age = 'Y')
				   |""".stripMargin

			override val params: List[String] = List(dob, juniorId.toString)
		}).nonEmpty
		if (notTooYoung) {
			ValidationOk
		} else {
			ValidationResult.from("Prospective junior members must be at least 10 years old by August 31st to participate in the Junior Program.")
		}
	}

	def doUpdate(pb: PersistenceBroker, data: RequiredInfoShape): Unit = {
		val updateQuery = new PreparedQueryForUpdateOrDelete(Set(MemberUserType)) {
			override def getQuery: String =
				s"""
				   |update persons set
				   |name_first=?,
				   |name_last=?,
				   |name_middle_initial=?,
				   |dob=to_date(?,'MM/DD/YYYY'),
				   |email=?,
				   |addr_1=?,
				   |addr_2=?,
				   |addr_3=?,
				   |city=?,
				   |state=?,
				   |zip=?,
				   |country=?,
				   |phone_primary=?,
				   |phone_primary_type=?,
				   |phone_alternate=?,
				   |phone_alternate_type=?,
				   |allergies=?,
				   |medications=?,
				   |special_needs=?
				   |where person_id = ?
              """.stripMargin

			override val params: List[String] = List(
				data.firstName.orNull,
				data.lastName.orNull,
				data.middleInitial.orNull,
				data.dob.orNull,
				data.childEmail.orNull,
				data.addr1.orNull,
				data.addr2.orNull,
				data.addr3.orNull,
				data.city.orNull,
				data.state.orNull,
				data.zip.orNull,
				data.country.orNull,
				data.primaryPhone.orNull,
				data.primaryPhoneType.orNull,
				data.alternatePhone.orNull,
				data.alternatePhoneType.orNull,
				data.allergies.orNull,
				data.medications.orNull,
				data.specialNeeds.orNull,
				data.personId.toString
			)
		}

		pb.executePreparedQueryForUpdateOrDelete(updateQuery)
	}
}
