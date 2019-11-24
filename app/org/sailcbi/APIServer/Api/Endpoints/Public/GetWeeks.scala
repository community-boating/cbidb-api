package org.sailcbi.APIServer.Api.Endpoints.Public

import java.time.LocalDate

import javax.inject.Inject
import org.sailcbi.APIServer.Api.Endpoints.Member.EmergencyContactShape
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.PublicUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, ResultSetWrapper}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext

class GetWeeks @Inject()(implicit exec: ExecutionContext) extends Controller {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action(req => {
		val parsedRequest = ParsedRequest(req)
		val rc = PA.getRequestCache(PublicUserType, None, parsedRequest).get
		val pb = rc.pb

		val q = new PreparedQueryForSelect[GetWeeksResult](Set(PublicUserType)) {
			override val params: List[String] = List.empty

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): GetWeeksResult = GetWeeksResult(
				rsw.getInt(1),
				rsw.getString(2),
				rsw.getLocalDate(3),
				rsw.getLocalDate(4)
			)

			override def getQuery: String =
				"""
				  |select
				  |week ,
				  |(case week when 0 then 'Spring' else 'Week '||week end),
				  |monday,
				  |monday + 4
				  |from jp_weeks
				  |where season = util_pkg.get_current_season
				  |and week between 0 and 10
				  |order by week
				  |""".stripMargin
		}

		val weeks = pb.executePreparedQueryForSelect(q).toArray
		implicit val format = GetWeeksResult.format
		Ok(Json.toJson(weeks))
	})

	case class GetWeeksResult(weekNumber: Int, weekTitle: String, monday: LocalDate, friday: LocalDate)

	object GetWeeksResult {
		implicit val format = Json.format[GetWeeksResult]

		def apply(v: JsValue): GetWeeksResult = v.as[GetWeeksResult]
	}
}
