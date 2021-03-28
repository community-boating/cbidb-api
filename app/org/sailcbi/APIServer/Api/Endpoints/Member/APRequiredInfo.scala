package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.framework.API.{ValidationError, ValidationOk, ValidationResult}
import org.sailcbi.APIServer.CbiUtil.{ParsedRequest, PhoneUtil}
import org.sailcbi.APIServer.IO.PreparedQueries.{PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import org.sailcbi.APIServer.Services.Authentication.MemberRequestCache
import org.sailcbi.APIServer.Services.{PermissionsAuthority, RequestCache, ResultSetWrapper}
import play.api.libs.json.{JsNumber, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class APRequiredInfo @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(parsedRequest, rc => {
			val personId = rc.getAuthedPersonId()

			val select = new PreparedQueryForSelect[APRequiredInfoShape](Set(MemberRequestCache)) {
				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): APRequiredInfoShape =
					APRequiredInfoShape(
						namePrefix = rsw.getOptionString(1),
						firstName = rsw.getOptionString(2),
						lastName = rsw.getOptionString(3),
						middleInitial = rsw.getOptionString(4),
						nameSuffix = rsw.getOptionString(5),
						dob = rsw.getOptionString(6),
						addr1 = rsw.getOptionString(7),
						addr2 = rsw.getOptionString(8),
						addr3 = rsw.getOptionString(9),
						city = rsw.getOptionString(10),
						state = rsw.getOptionString(11),
						zip = rsw.getOptionString(12),
						country = rsw.getOptionString(13),
						primaryPhone = rsw.getOptionString(14),
						primaryPhoneType = rsw.getOptionString(15),
						alternatePhone = rsw.getOptionString(16),
						alternatePhoneType = rsw.getOptionString(17),
						allergies = rsw.getOptionString(18),
						medications = rsw.getOptionString(19),
						specialNeeds = rsw.getOptionString(20)
					)

				override def getQuery: String =
					s"""
					   |select
					   |NAME_PREFIX,
					   |name_first,
					   |name_last,
					   |name_middle_initial,
					   |NAME_SUFFIX,
					   |to_char(dob, 'MM/DD/YYYY'),
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

				override val params: List[String] = List(personId.toString)
			}

			val resultObj = rc.executePreparedQueryForSelect(select).head
			implicit val format = APRequiredInfoShape.format
			val resultJson: JsValue = Json.toJson(resultObj)
			Future(Ok(resultJson))
		})
	}

	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, APRequiredInfoShape.apply)(parsed => {
			PA.withRequestCacheMember(parsedRequest, rc => {
				val personId = rc.getAuthedPersonId()
				runValidations(parsed, rc, personId) match {
					case ve: ValidationError => Future(Ok(ve.toResultError.asJsObject()))
					case ValidationOk => {
						doUpdate(rc, parsed, personId)
						Future(Ok(new JsObject(Map(
							"personId" -> JsNumber(personId)
						))))
					}
				}
			})
		})
	}

	def runValidations(parsed: APRequiredInfoShape, rc: RequestCache, personId: Int): ValidationResult = {
		val dob = parsed.dob.getOrElse("")

		val unconditionalValidations = List(
			tooYoung(rc, dob),
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
			ValidationResult.inline(parsed.primaryPhone)(
				phone => PhoneUtil.regex.findFirstIn(phone.getOrElse("")).isDefined,
				"Primary Phone is not valid."
			)
		)

		val conditionalValidations = List((
			parsed.alternatePhone.isDefined,
			ValidationResult.inline(parsed.alternatePhone)(
				phone => PhoneUtil.regex.findFirstIn(phone.getOrElse("")).isDefined,
				"Alternate Phone is not valid."
			)
		), (
			parsed.alternatePhone.isDefined,
			ValidationResult.checkBlankCustom(parsed.alternatePhoneType, "Alternate phone type must be specified if alternate phone number is provided."),
		)).filter(_._1).map(_._2)

		// Run all validations and combine into one
		ValidationResult.combine(unconditionalValidations ::: conditionalValidations)
	}


	def tooYoung(rc: RequestCache, dob: String): ValidationResult = {
		val notTooYoung = rc.executePreparedQueryForSelect(new PreparedQueryForSelect[Boolean](Set(MemberRequestCache)) {
			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Boolean = rs.getString(1).equals("Y")

			override def getQuery: String =
				s"""
				   |select 1 from dual
				   |   where util_pkg.age(to_date(?,'MM/DD/YYYY'), util_pkg.get_sysdate) >= 18
				   |""".stripMargin

			override val params: List[String] = List(dob)
		}).nonEmpty

		if (notTooYoung) {
			ValidationOk
		} else {
			ValidationResult.from("Prospective adult members must be at least 18 years old.")
		}
	}

	def doUpdate(rc: RequestCache, data: APRequiredInfoShape, personId: Int): Unit = {
		val updateQuery = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
			override def getQuery: String =
				s"""
				   |update persons set
				   |NAME_PREFIX = ?,
				   |NAME_FIRST = ?,
				   |NAME_MIDDLE_INITIAL = ?,
				   |NAME_LAST = ?,
				   |NAME_SUFFIX = ?,
				   |DOB = to_date(?,'MM/DD/YYYY'),
				   |ADDR_1 = ?,
				   |ADDR_2 = ?,
				   |ADDR_3 = ?,
				   |CITY = ?,
				   |STATE = ?,
				   |ZIP = ?,
				   |COUNTRY = ?,
				   |PHONE_PRIMARY = ?,
				   |PHONE_PRIMARY_TYPE = ?,
				   |PHONE_ALTERNATE = ?,
				   |PHONE_ALTERNATE_TYPE = ?,
				   |ALLERGIES = ?,
				   |MEDICATIONS = ?,
				   |SPECIAL_NEEDS = ?
				   |where person_id = ?
              """.stripMargin

			override val params: List[String] = List(
				data.namePrefix.orNull,
				data.firstName.orNull,
				data.middleInitial.orNull,
				data.lastName.orNull,
				data.nameSuffix.orNull,
				data.dob.orNull,
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
				personId.toString
			)
		}

		rc.executePreparedQueryForUpdateOrDelete(updateQuery)
	}

	case class APRequiredInfoShape(
		namePrefix: Option[String],
		firstName: Option[String],
		lastName: Option[String],
		middleInitial: Option[String],
		nameSuffix: Option[String],
		dob: Option[String],
		addr1: Option[String],
		addr2: Option[String],
		addr3: Option[String],
		city: Option[String],
		state: Option[String],
		zip: Option[String],
		country: Option[String],
		primaryPhone: Option[String],
		primaryPhoneType: Option[String],
		alternatePhone: Option[String],
		alternatePhoneType: Option[String],
		allergies: Option[String],
		medications: Option[String],
		specialNeeds: Option[String]
	)

	object APRequiredInfoShape {
		implicit val format = Json.format[APRequiredInfoShape]

		def apply(v: JsValue): APRequiredInfoShape = v.as[APRequiredInfoShape]
	}
}