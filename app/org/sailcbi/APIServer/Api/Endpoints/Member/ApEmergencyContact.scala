package org.sailcbi.APIServer.Api.Endpoints.Member

import org.sailcbi.APIServer.Api.{ValidationError, ValidationOk, ValidationResult}
import org.sailcbi.APIServer.CbiUtil.{ParsedRequest, PhoneUtil}
import org.sailcbi.APIServer.IO.PreparedQueries.{PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker, ResultSetWrapper}
import play.api.libs.json.{JsNumber, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApEmergencyContact @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(None, parsedRequest, rc => {
			val pb = rc.pb
			val personId = rc.auth.getAuthedPersonId(rc)

			val select = new PreparedQueryForSelect[ApEmergencyContactShape](Set(MemberUserType)) {
				override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): ApEmergencyContactShape =
					ApEmergencyContactShape(
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

				override val params: List[String] = List(personId.toString)
			}

			val resultObj = rc.executePreparedQueryForSelect(select).head
			implicit val format = ApEmergencyContactShape.format
			val resultJson: JsValue = Json.toJson(resultObj)
			Future(Ok(resultJson))
		})
	}

	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(request.body.asJson, ApEmergencyContactShape.apply)(parsed => {
			PA.withRequestCacheMember(None, parsedRequest, rc => {
				val pb = rc.pb
				val personId = rc.auth.getAuthedPersonId(rc)

				runValidations(parsed, pb) match {
					case ve: ValidationError => Future(Ok(ve.toResultError.asJsObject()))
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
								personId.toString
							)
						}

						rc.executePreparedQueryForUpdateOrDelete(updateQuery)

						Future(Ok(new JsObject(Map(
							"personId" -> JsNumber(personId)
						))))
					}
				}
			})
		})
	}

	def runValidations(parsed: ApEmergencyContactShape, pb: PersistenceBroker): ValidationResult = {
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

	case class ApEmergencyContactShape(
		emerg1Name: Option[String],
		emerg1Relation: Option[String],
		emerg1PhonePrimary: Option[String],
		emerg1PhonePrimaryType: Option[String],
		emerg1PhoneAlternate: Option[String],
		emerg1PhoneAlternateType: Option[String],

		emerg2Name: Option[String],
		emerg2Relation: Option[String],
		emerg2PhonePrimary: Option[String],
		emerg2PhonePrimaryType: Option[String],
		emerg2PhoneAlternate: Option[String],
		emerg2PhoneAlternateType: Option[String]
	)

	object ApEmergencyContactShape {
		implicit val format = Json.format[ApEmergencyContactShape]

		def apply(v: JsValue): ApEmergencyContactShape = v.as[ApEmergencyContactShape]
	}

}