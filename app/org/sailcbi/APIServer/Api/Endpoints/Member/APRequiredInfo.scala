package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.Api.{ValidationError, ValidationOk, ValidationResult}
import org.sailcbi.APIServer.CbiUtil.{JsValueWrapper, ParsedRequest, PhoneUtil}
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.IO.PreparedQueries.{PreparedQueryForInsert, PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.{CacheBroker, PermissionsAuthority, PersistenceBroker, ResultSetWrapper}
import play.api.libs.json.{JsNumber, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.{ExecutionContext, Future}

class APRequiredInfo @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(None, parsedRequest, rc => {
			val pb: PersistenceBroker = rc.pb
			val personId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)

			val select = new PreparedQueryForSelect[APRequiredInfoShape](Set(MemberUserType)) {
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

			val resultObj = pb.executePreparedQueryForSelect(select).head
			implicit val format = APRequiredInfoShape.format
			val resultJson: JsValue = Json.toJson(resultObj)
			Future(Ok(resultJson))
		})
	}

	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, APRequiredInfoShape.apply)(parsed => {
			PA.withRequestCacheMember(None, parsedRequest, rc => {
				val pb = rc.pb
				val personId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)
				runValidations(parsed, pb, personId) match {
					case ve: ValidationError => Future(Ok(ve.toResultError.asJsObject()))
					case ValidationOk => {
						doUpdate(pb, parsed, personId)
						Future(Ok(new JsObject(Map(
							"personId" -> JsNumber(personId)
						))))
					}
				}
			})
		})
	}

	def runValidations(parsed: APRequiredInfoShape, pb: PersistenceBroker, personId: Int): ValidationResult = {
		ValidationOk
//		val dob = parsed.dob.getOrElse("")
//
//		val unconditionalValidations = List(
//			tooOld(pb, dob),
//			tooYoung(pb, dob, juniorId),
//			cannotAlterDOB(pb, dob, juniorId),
//			ValidationResult.checkBlank(parsed.firstName, "First Name"),
//			ValidationResult.checkBlank(parsed.lastName, "Last Name"),
//			ValidationResult.checkBlank(parsed.dob, "Date of Birth"),
//			ValidationResult.checkBlank(parsed.addr1, "Address"),
//			ValidationResult.checkBlank(parsed.city, "City"),
//			ValidationResult.checkBlank(parsed.state, "State"),
//			ValidationResult.checkBlank(parsed.zip, "Zip code"),
//			ValidationResult.checkBlank(parsed.primaryPhone, "Primary phone number"),
//			ValidationResult.checkBlank(parsed.primaryPhoneType, "Primary phone number type"),
//			ValidationResult.inline(parsed.zip)(zip => "^[0-9]{5}(-[0-9]{4})?$".r.findFirstIn(zip.getOrElse("")).isDefined, "Zip code is an invalid format."),
//			ValidationResult.inline(parsed.childEmail)(
//				childEmail => childEmail.getOrElse("").length == 0 || "^[A-Za-z0-9._%-]+@[A-Za-z0-9._%-]+\\.[A-Za-z]{2,4}$".r.findFirstIn(childEmail.getOrElse("")).isDefined,
//				"Child email is not valid."
//			),
//			ValidationResult.inline(parsed.primaryPhone)(
//				phone => PhoneUtil.regex.findFirstIn(phone.getOrElse("")).isDefined,
//				"Primary Phone is not valid."
//			)
//		)
//
//		val conditionalValidations = List((
//				parsed.alternatePhone.isDefined,
//				ValidationResult.inline(parsed.alternatePhone)(
//					phone => PhoneUtil.regex.findFirstIn(phone.getOrElse("")).isDefined,
//					"Alternate Phone is not valid."
//				)
//		), (
//				parsed.alternatePhone.isDefined,
//				ValidationResult.checkBlankCustom(parsed.alternatePhoneType, "Alternate phone type must be specified if alternate phone number is provided."),
//		)).filter(_._1).map(_._2)
//
//		// Run all validations and combine into one
//		ValidationResult.combine(unconditionalValidations ::: conditionalValidations)
	}

//	def cannotAlterDOB(pb: PersistenceBroker, dob: String, juniorId: Option[Int]): ValidationResult = juniorId match {
//		case None => ValidationOk
//		case Some(id) => {
//			val (existingDOB, currentSeason, firstMembershipYear) =
//				pb.executePreparedQueryForSelect(new PreparedQueryForSelect[(String, Int, Option[Int])](Set(MemberUserType)) {
//					override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (String, Int, Option[Int]) = (rsw.getString(1), rsw.getInt(2), rsw.getOptionInt(3))
//
//					override def getQuery: String =
//						"""
//						  |select to_char(dob, 'MM/DD/YYYY'), util_pkg.get_current_season, min(to_char(expiration_date,'YYYY')) from persons p left outer join persons_memberships pm
//						  |on p.person_id = pm.person_id where p.person_id = ? group by to_char(dob, 'MM/DD/YYYY'), util_pkg.get_current_season
//						  |""".stripMargin
//
//					override val params: List[String] = List(id.toString)
//				}).head
//			if (existingDOB == dob) ValidationOk
//			else if (firstMembershipYear.getOrElse(currentSeason) == currentSeason) ValidationOk
//			else ValidationResult.from("DOB cannot be altered.  If DOB is inaccurate please email the Front Office at info@community-boating.org")
//		}
//	}
//
//	def tooOld(pb: PersistenceBroker, dob: String): ValidationResult = {
//		val notTooOld = pb.executePreparedQueryForSelect(new PreparedQueryForSelect[Boolean](Set(MemberUserType)) {
//			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Boolean = rs.getString(1).equals("Y")
//
//			override def getQuery: String =
//				s"""
//				   |select person_pkg.is_jp_max_age(to_date(?,'MM/DD/YYYY')) from dual
//				   |""".stripMargin
//
//			override val params: List[String] = List(dob)
//		}).head
//		if (notTooOld) {
//			ValidationOk
//		} else {
//			ValidationResult.from("Prospective juniors must be 17 or younger and may not turn 18 before the program begins.")
//		}
//	}
//
//	def tooYoung(pb: PersistenceBroker, dob: String, juniorId: Option[Int]): ValidationResult = {
//		val notTooYoung = pb.executePreparedQueryForSelect(new PreparedQueryForSelect[Boolean](Set(MemberUserType)) {
//			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Boolean = rs.getString(1).equals("Y")
//
//			override def getQuery: String =
//				s"""
//				   |select 1 from dual
//				   |   where person_pkg.is_jp_min_age(to_date(?,'MM/DD/YYYY')) = 'Y'
//				   |   or exists (select 1 from persons where person_id = ? and ignore_jp_min_age = 'Y')
//				   |""".stripMargin
//
//			// TODO: redo this without a sentinel
//			override val params: List[String] = List(dob, juniorId.getOrElse(-999).toString)
//		}).nonEmpty
//		if (notTooYoung) {
//			ValidationOk
//		} else {
//			ValidationResult.from("Prospective junior members must be at least 10 years old by August 31st to participate in the Junior Program.")
//		}
//	}

	def doUpdate(pb: PersistenceBroker, data: APRequiredInfoShape, personId: Int): Unit = {
		val updateQuery = new PreparedQueryForUpdateOrDelete(Set(MemberUserType)) {
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

		pb.executePreparedQueryForUpdateOrDelete(updateQuery)
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