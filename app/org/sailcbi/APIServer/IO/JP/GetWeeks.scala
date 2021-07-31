package org.sailcbi.APIServer.IO.JP

import com.coleji.framework.Core.RequestCache
import com.coleji.framework.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.framework.Storable.ResultSetWrapper
import org.sailcbi.APIServer.UserTypes.{PublicRequestCache, StaffRequestCache}
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDate

object GetWeeks {
	def getWeeks(rc: RequestCache): Array[GetWeeksResult] = {
		val q = new PreparedQueryForSelect[GetWeeksResult](Set(PublicRequestCache, StaffRequestCache)) {
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
				  |and week between 0 and 11
				  |order by week
				  |""".stripMargin
		}

		rc.executePreparedQueryForSelect(q).toArray
	}

	case class GetWeeksResult(weekNumber: Int, weekTitle: String, monday: LocalDate, friday: LocalDate)

	object GetWeeksResult {
		implicit val format = Json.format[GetWeeksResult]

		def apply(v: JsValue): GetWeeksResult = v.as[GetWeeksResult]
	}
}
