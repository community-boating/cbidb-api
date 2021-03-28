package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.framework.IO.PreparedQueries.{PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import org.sailcbi.APIServer.CbiUtil.{GetSQLLiteralPrepared, ParsedRequest}
import org.sailcbi.APIServer.Services.Authentication.MemberRequestCache
import org.sailcbi.APIServer.Services._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SurveyInfo @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get(juniorId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMemberWithJuniorId(parsedRequest, juniorId, rc => {
			val cb: CacheBroker = rc.cb

			val select = new PreparedQueryForSelect[SurveyInfoShape](Set(MemberRequestCache)) {
				override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): SurveyInfoShape =
					SurveyInfoShape(
						juniorId,
						rs.getOptionString(1),
						rs.getOptionString(2).map(s => s.split(":")),
						rs.getOptionString(3),
						rs.getOptionString(4),
						rs.getOptionString(5).map(s => s.split(":")),
						rs.getOptionString(6),
						rs.getOptionString(7),
						rs.getOptionString(8).map(_.equals("Y"))
					)

				override def getQuery: String =
					s"""
					   |select gender, referral_source, referral_other, language, ethnicity, ethnicity_other, school, reduced_lunch
					   |from persons where person_id = ?
					""".stripMargin

				override val params: List[String] = List(juniorId.toString)
			}

			val resultObj = rc.executePreparedQueryForSelect(select).head
			val resultJson: JsValue = Json.toJson(resultObj)
			Future(Ok(resultJson))
		})
	}

	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		val juniorId: Int = request.body.asJson.map(json => json("personId").toString().toInt).get
		PA.withRequestCacheMemberWithJuniorId(parsedRequest, juniorId, rc => {

			val cb: CacheBroker = rc.cb
			val data = request.body.asJson
			PA.withParsedPostBodyJSON(request.body.asJson, SurveyInfoShape.apply)(parsed => {
				val updateQuery = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
					override def getQuery: String =
						s"""
						   |update persons set
						   |gender = ?,
						   |referral_source = ?,
						   |referral_other = ?,
						   |language = ?,
						   |ethnicity = ?,
						   |ethnicity_other = ?,
						   |school = ?,
						   |reduced_lunch = ?
						   |where person_id = ?
		  				""".stripMargin

					override val params: List[String] = List(
						parsed.genderID.orNull,
						parsed.referral.map(_.mkString(":")).orNull,
						parsed.referralOther.orNull,
						parsed.language.orNull,
						parsed.ethnicity.map(_.mkString(":")).orNull,
						parsed.ethnicityOther.orNull,
						parsed.school.orNull,
						parsed.freeLunch.map(GetSQLLiteralPrepared.apply).orNull,
						parsed.personId.toString
					)
				}

				rc.executePreparedQueryForUpdateOrDelete(updateQuery)

				Future(Ok("done"))
			})
		})
	}
}
