package org.sailcbi.APIServer.Api.Endpoints.Public

import com.coleji.framework.Core.PermissionsAuthority
import com.coleji.framework.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services.ResultSetWrapper
import org.sailcbi.APIServer.UserTypes.PublicRequestCache
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetWeeks @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async (req => {
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCache(PublicRequestCache)(None, parsedRequest, rc => {


			val q = new PreparedQueryForSelect[GetWeeksResult](Set(PublicRequestCache)) {
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

			val weeks = rc.executePreparedQueryForSelect(q).toArray
			implicit val format = GetWeeksResult.format
			Future(Ok(Json.toJson(weeks)))
		})

	})

	case class GetWeeksResult(weekNumber: Int, weekTitle: String, monday: LocalDate, friday: LocalDate)

	object GetWeeksResult {
		implicit val format = Json.format[GetWeeksResult]

		def apply(v: JsValue): GetWeeksResult = v.as[GetWeeksResult]
	}
}
