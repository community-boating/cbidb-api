package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.framework.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.framework.IO.PreparedQueries.{PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import com.coleji.framework.Storable.ResultSetWrapper
import org.sailcbi.APIServer.CbiUtil.GetSQLLiteralPrepared
import org.sailcbi.APIServer.UserTypes.MemberRequestCache
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApSurveyInfo @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(parsedRequest, rc => {
			val personId = rc.getAuthedPersonId()

			val select = new PreparedQueryForSelect[ApSurveyInfoShape](Set(MemberRequestCache)) {
				override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): ApSurveyInfoShape =
					ApSurveyInfoShape(
						genderID = rs.getOptionString(1),
						referral = rs.getOptionString(2).map(s => s.split(":")),
						referralOther = rs.getOptionString(3),
						occupation = rs.getOptionString(4),
						employer = rs.getOptionString(5),
						matchingContributions = rs.getOptionString(6),
						language = rs.getOptionString(7),
						ethnicity = rs.getOptionString(8).map(s => s.split(":")),
						ethnicityOther = rs.getOptionString(9),
						student = rs.getOptionString(10),
						school = rs.getOptionString(11)
					)

				override def getQuery: String =
					s"""
					   |select
					   |gender,
					   |referral_source,
					   |referral_other,
					   |OCCUPATION,
					   |EMPLOYER,
					   |MATCHING_GIFTS,
					   |language,
					   |ethnicity,
					   |ethnicity_other,
					   |student,
					   |school
					   |from persons where person_id = ?
					""".stripMargin

				override val params: List[String] = List(personId.toString)
			}

			val resultObj = rc.executePreparedQueryForSelect(select).head
			println(resultObj)
			implicit val format = ApSurveyInfoShape.format
			val resultJson: JsValue = Json.toJson(resultObj)
			println(resultJson)
			Future(Ok(resultJson))
		})
	}

	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(parsedRequest, rc => {
			val personId = rc.getAuthedPersonId()
			PA.withParsedPostBodyJSON(request.body.asJson, ApSurveyInfoShape.apply)(parsed => {
				val updateQuery = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache)) {
					override def getQuery: String =
						s"""
						   |update persons set
						   |gender = ?,
						   |referral_source = ?,
						   |referral_other = ?,
						   |OCCUPATION = ?,
						   |EMPLOYER = ?,
						   |MATCHING_GIFTS = ?,
						   |language = ?,
						   |ethnicity = ?,
						   |ethnicity_other = ?,
						   |student = ?,
						   |school = ?
						   |where person_id = ?
		  				""".stripMargin

					override val params: List[String] = List(
						parsed.genderID.orNull,
						parsed.referral.map(_.mkString(":")).orNull,
						parsed.referralOther.orNull,
						parsed.occupation.orNull,
						parsed.employer.orNull,
						parsed.matchingContributions.orNull,
						parsed.language.orNull,
						parsed.ethnicity.map(_.mkString(":")).orNull,
						parsed.ethnicityOther.orNull,
						parsed.student.map(GetSQLLiteralPrepared.apply).orNull,
						parsed.school.orNull,
						personId.toString
					)
				}

				rc.executePreparedQueryForUpdateOrDelete(updateQuery)

				Future(Ok("done"))
			})
		})
	}

	case class ApSurveyInfoShape(
		genderID: Option[String],
		referral: Option[Array[String]],
		referralOther: Option[String],
		occupation: Option[String],
		employer: Option[String],
		matchingContributions: Option[String],
		language: Option[String],
		ethnicity: Option[Array[String]],
		ethnicityOther: Option[String],
		student: Option[String],
		school: Option[String]
	)

	object ApSurveyInfoShape {
		implicit val format = Json.format[ApSurveyInfoShape]

		def apply(v: JsValue): ApSurveyInfoShape = v.as[ApSurveyInfoShape]
	}
}
