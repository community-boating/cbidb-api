package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.framework.API.{ValidationError, ValidationOk, ValidationResult}
import com.coleji.framework.Core.{CacheBroker, ParsedRequest, PermissionsAuthority}
import com.coleji.framework.IO.PreparedQueries.{PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import com.coleji.framework.Storable.ResultSetWrapper
import com.coleji.framework.Util.PhoneUtil
import org.sailcbi.APIServer.UserTypes.MemberRequestCache
import play.api.libs.json.{JsNumber, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EmergencyContact @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get(juniorId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMemberWithJuniorId(parsedRequest, juniorId, rc => {
			val cb: CacheBroker = rc.cb

			val select = new PreparedQueryForSelect[EmergencyContactShape](Set(MemberRequestCache)) {
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

			val resultObj = rc.executePreparedQueryForSelect(select).head
			val resultJson: JsValue = Json.toJson(resultObj)
			Future(Ok(resultJson))
		})
	}

	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(request.body.asJson, EmergencyContactShape.apply)(parsed => {
			val juniorId = request.body.asJson.map(json => json("personId").toString().toInt).get
			PA.withRequestCacheMemberWithJuniorId(parsedRequest, juniorId, rc => {

				val cb: CacheBroker = rc.cb
				runValidations(parsed, None) match {
					case ve: ValidationError => Future(Ok(ve.toResultError.asJsObject()))
					case ValidationOk => {
						val updateQuery = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
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

						rc.executePreparedQueryForUpdateOrDelete(updateQuery)

						Future(Ok(new JsObject(Map(
							"personId" -> JsNumber(parsed.personId)
						))))
					}
				}
			})
		})
	}

	def runValidations(parsed: EmergencyContactShape, juniorId: Option[Int]): ValidationResult = {
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
