package org.sailcbi.APIServer.IO.PreparedQueries.Public

import org.sailcbi.APIServer.IO.PreparedQueries.HardcodedQueryForSelectCastableToJSObject
import org.sailcbi.APIServer.Services.Authentication.PublicRequestCache
import org.sailcbi.APIServer.Services.ResultSetWrapper
import play.api.libs.json.{JsArray, Json}

class GetJpTeams extends HardcodedQueryForSelectCastableToJSObject[GetJpTeamsResult](Set(PublicRequestCache)) {
	val getQuery: String =
		"""
		  |select t.team_id, t.team_name, nvl(sum(points),0)
		  |from jp_team_event_points tep
		  |inner join jp_team_events e
		  |on tep.event_id = e.event_id
		  |and to_char(e.awarded_date,'YYYY') = to_char(sysdate,'YYYY')
		  |right outer join jp_teams t
		  |on t.team_id = tep.team_id
		  |group by t.team_id, t.team_name
		""".stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): GetJpTeamsResult = GetJpTeamsResult(
		rs.getInt(1),
		rs.getString(2),
		rs.getInt(3)
	)

	val columnNames = List(
		"TEAM_ID",
		"TEAM_NAME",
		"POINTS"
	)

	def mapCaseObjectToJsArray(caseObject: GetJpTeamsResult): JsArray = JsArray(IndexedSeq(
		Json.toJson(caseObject.teamName),
		Json.toJson(caseObject.points)
	))
}

case class GetJpTeamsResult(
	teamId: Int,
	teamName: String,
	points: Int
)