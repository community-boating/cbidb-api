package org.sailcbi.APIServer.IO.PreparedQueries.Member

import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.MemberRequestCache
import org.sailcbi.APIServer.Services.ResultSetWrapper
import play.api.libs.json.Json

// TODO: replace with entity-based arch
class GetChildDataQuery(parentId: Int) extends PreparedQueryForSelect[GetChildDataQueryResult](allowedUserTypes = Set(MemberRequestCache)) {
	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): GetChildDataQueryResult =
		GetChildDataQueryResult(
			rs.getInt(1),
			rs.getString(2),
			rs.getString(3),
			rs.getString(4),
			rs.getString(5),
			rs.getString(6)
		)

	override def getQuery: String =
		"""
		  |select
		  |p.person_id,
		  |p.name_first,
		  |p.name_last,
		  |jp_state_pkg.jp_status(p.person_id,cc_pkg.get_order_id(?)) as status,
		  |jp_state_pkg.jp_actions(p.person_id,cc_pkg.get_order_id(?), 'Y') as actions,
		  |ratings_pkg.jp_ratings(p.person_id)
		  |from persons p
		  |left outer join jp_teams t on p.jp_team_id = t.team_id
		  |left outer join active_jp_members v on p.person_id = v.person_id
		  |where p.person_id in (select b from acct_links where a = ? and type_id = 2)
		  |and (p.dob is null or person_pkg.is_jp_max_age(nvl(p.dob,sysdate)) = 'Y' or p.jp_age_override = 'Y' or v.person_id is not null)
		  |order by p.person_id
		  |""".stripMargin

	override val params: List[String] = List(parentId.toString, parentId.toString, parentId.toString)
}

case class GetChildDataQueryResult(
	personId: Int,
	nameFirst: String,
	nameLast: String,
	status: String,
	actions: String,
	ratings: String
)

object GetChildDataQueryResult {
	implicit val format = Json.format[GetChildDataQueryResult]
}