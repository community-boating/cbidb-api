package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.Api.{ValidationError, ValidationOk, ValidationResult}
import org.sailcbi.APIServer.CbiUtil.{JsValueWrapper, ParsedRequest, PhoneUtil}
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.IO.Junior.JPPortal
import org.sailcbi.APIServer.IO.PreparedQueries.{PreparedQueryForInsert, PreparedQueryForSelect, PreparedQueryForUpdateOrDelete, PreparedValue}
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services._
import play.api.libs.json.{JsNumber, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.{ExecutionContext, Future}

class RequiredInfo @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get(juniorId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMemberWithJuniorId(None, parsedRequest, juniorId, rc => {
			val pb: PersistenceBroker = rc.pb
			val cb: CacheBroker = rc.cb

			val select = new PreparedQueryForSelect[RequiredInfoShape](Set(MemberUserType)) {
				override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): RequiredInfoShape =
					RequiredInfoShape(
						Some(juniorId),
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
			Future(Ok(resultJson))
		})
	}

	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, RequiredInfoShape.apply)(parsed => {
			import JsValueWrapper.wrapJsValue
			request.body.asJson.map(json => json.getNonNull("personId")).get match {
				case Some(id: JsValue) => {
					val juniorId: Int = id.toString().toInt
					println(s"its an update: $juniorId")
					PA.withRequestCacheMemberWithJuniorId(None, parsedRequest, juniorId, rc => {
						val pb = rc.pb
						runValidations(parsed, pb, Some(id.toString().toInt)) match {
							case ve: ValidationError => Future(Ok(ve.toResultError.asJsObject()))
							case ValidationOk => {
								doUpdate(pb, parsed, MemberUserType.getAuthedPersonId(rc.auth.userName, pb))
								Future(Ok(new JsObject(Map(
									"personId" -> JsNumber(juniorId)
								))))
							}
						}
					})

				}
				case None => {
					println(s"its a create")
					PA.withRequestCacheMember(None, parsedRequest, rc => {
						val pb = rc.pb
						runValidations(parsed, pb, None) match {
							case ve: ValidationError => Future(Ok(ve.toResultError.asJsObject()))
							case ValidationOk => {
								val newJuniorId = doCreate(pb, parsed, MemberUserType.getAuthedPersonId(rc.auth.userName, pb))
								Future(Ok(new JsObject(Map(
									"personId" -> JsNumber(newJuniorId)
								))))
							}
						}
					})

				}
			}
		})
	}

	def runValidations(parsed: RequiredInfoShape, pb: PersistenceBroker, juniorId: Option[Int]): ValidationResult = {
		val dob = parsed.dob.getOrElse("")

		val unconditionalValidations = List(
			tooOld(pb, dob),
			tooYoung(pb, dob, juniorId),
			cannotAlterDOB(pb, dob, juniorId),
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
				childEmail => childEmail.getOrElse("").length == 0 || "^[A-Za-z0-9._%-]+@[A-Za-z0-9._%-]+\\.[A-Za-z]{2,4}$".r.findFirstIn(childEmail.getOrElse("")).isDefined,
				"Child email is not valid."
			),
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

	def cannotAlterDOB(pb: PersistenceBroker, dob: String, juniorId: Option[Int]): ValidationResult = juniorId match {
		case None => ValidationOk
		case Some(id) => {
			val (existingDOB, currentSeason, firstMembershipYear) =
				pb.executePreparedQueryForSelect(new PreparedQueryForSelect[(String, Int, Option[Int])](Set(MemberUserType)) {
					override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (String, Int, Option[Int]) = (rsw.getString(1), rsw.getInt(2), rsw.getOptionInt(3))

					override def getQuery: String =
						"""
						  |select to_char(dob, 'MM/DD/YYYY'), util_pkg.get_current_season, min(to_char(expiration_date,'YYYY')) from persons p left outer join persons_memberships pm
						  |on p.person_id = pm.person_id where p.person_id = ? group by to_char(dob, 'MM/DD/YYYY'), util_pkg.get_current_season
						  |""".stripMargin

					override val params: List[String] = List(id.toString)
				}).head
			if (existingDOB == dob) ValidationOk
			else if (firstMembershipYear.getOrElse(currentSeason) == currentSeason) ValidationOk
			else ValidationResult.from("DOB cannot be altered.  If DOB is inaccurate please email the Front Office at info@community-boating.org")
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

	def tooYoung(pb: PersistenceBroker, dob: String, juniorId: Option[Int]): ValidationResult = {
		val notTooYoung = pb.executePreparedQueryForSelect(new PreparedQueryForSelect[Boolean](Set(MemberUserType)) {
			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Boolean = rs.getString(1).equals("Y")

			override def getQuery: String =
				s"""
				   |select 1 from dual
				   |   where person_pkg.is_jp_min_age(to_date(?,'MM/DD/YYYY')) = 'Y'
				   |   or exists (select 1 from persons where person_id = ? and ignore_jp_min_age = 'Y')
				   |""".stripMargin

			// TODO: redo this without a sentinel
			override val params: List[String] = List(dob, juniorId.getOrElse(-999).toString)
		}).nonEmpty
		if (notTooYoung) {
			ValidationOk
		} else {
			ValidationResult.from("Prospective junior members must be at least 10 years old by August 31st to participate in the Junior Program.")
		}
	}

	def doCreate(pb: PersistenceBroker, data: RequiredInfoShape, parentPersonId: Int): Int = {
		val createPersonQuery = new PreparedQueryForInsert(Set(MemberUserType)) {
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
				data.specialNeeds.orNull
			)
			override val pkName: Option[String] = Some("PERSON_ID")

			override def getQuery: String =
				"""
				  |insert into persons (
				  |name_first,
				  |name_last,
				  |name_middle_initial,
				  |dob,
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
				  |special_needs,
				  |temp
				  |) values (
				  |?, ?, ?, to_date(?,'MM/DD/YYYY'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'P'
				  |)
				  |""".stripMargin
		}
		val juniorPersonId = pb.executePreparedQueryForInsert(createPersonQuery).get.toInt

		val createRelationshipQuery = new PreparedQueryForInsert(Set(MemberUserType)) {
			override val params: List[String] = List(
				parentPersonId.toString,
				juniorPersonId.toString,
				MagicIds.PERSON_RELATIONSHIP_TYPE_PARENT_WITH_ACCT_LINK.toString
			)
			override val pkName: Option[String] = None

			override def getQuery: String =
				"""
				  |insert into person_relationships(a,b,type_id) values (?, ?, ?)
				  |""".stripMargin
		}

		pb.executePreparedQueryForInsert(createRelationshipQuery)

		JPPortal.addSCMIfNotMember(pb, parentPersonId, juniorPersonId)

		juniorPersonId
	}

	def doUpdate(pb: PersistenceBroker, data: RequiredInfoShape, parentPersonId: Int): Unit = {
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
				   |special_needs=?,
				   |proto_state = (case proto_state when '${MagicIds.PERSONS_PROTO_STATE.IS_PROTO}' then '${MagicIds.PERSONS_PROTO_STATE.WAS_PROTO}' else proto_state end)
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
				data.personId.get.toString
			)
		}

		pb.executePreparedQueryForUpdateOrDelete(updateQuery)
		JPPortal.addSCMIfNotMember(pb, parentPersonId, data.personId.get)
	}
}
