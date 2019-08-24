package org.sailcbi.APIServer.IO.PreparedQueries.Member

import java.sql.ResultSet

import org.sailcbi.APIServer.IO.PreparedQueries.{HardcodedQueryForSelect, PreparedQueryForSelect}
import org.sailcbi.APIServer.Services.Authentication.{MemberUserType, PublicUserType}
import play.api.libs.json.Json

object GetClassInstancesQuery {
	def byJunior(week: Option[Int], typeId: Int, juniorId: Int): HardcodedQueryForSelect[GetClassInstancesQueryResult] = {
		new HardcodedQueryForSelect[GetClassInstancesQueryResult](Set(MemberUserType)) {
			override def getQuery: String = query(week, typeId, Some(juniorId))

			override def mapResultSetRowToCaseObject(rs: ResultSet): GetClassInstancesQueryResult = mapResultSetRowToCaseObjectImpl(rs)
		}
	}

	def public(week: Option[Int], typeId: Int): HardcodedQueryForSelect[GetClassInstancesQueryResult] = {
		new HardcodedQueryForSelect[GetClassInstancesQueryResult](Set(PublicUserType)) {
			override def getQuery: String = query(week, typeId, None)

			override def mapResultSetRowToCaseObject(rs: ResultSet): GetClassInstancesQueryResult = mapResultSetRowToCaseObjectImpl(rs)
		}
	}

	def mapResultSetRowToCaseObjectImpl(rs: ResultSet): GetClassInstancesQueryResult = GetClassInstancesQueryResult(
		rs.getInt(1),
		rs.getString(2),
		rs.getString(3),
		rs.getString(4),
		rs.getString(5),
		rs.getString(6),
		rs.getString(7),
		rs.getString(8)
	)

	def query(week: Option[Int], typeId: Int, juniorId: Option[Int]): String = {
		val reserveEligible = juniorId match {
			case Some(j) => s"jp_class_pkg.is_reserve_eligible(i.instance_id,${j})"
			case None => "'N'"
		}

		val weekString = week.getOrElse("null")

		s"""
		   |select
		   |i.instance_id,
		   |nvl(i.name_override,t.type_name) as class_name,
		   |to_char(s1.session_datetime, 'Day')||'<br>'||to_char(s1.session_datetime,'MM/DD/YYYY') as first_day,
		   |to_char(s2.session_datetime, 'Day')||'<br>'||to_char(s2.session_datetime,'MM/DD/YYYY') as last_day,
		   |to_char(s1.session_datetime, 'HH:MIPM')||' - '||to_char(s1.session_datetime+(nvl(s1.length_override,t.session_length)/24), 'HH:MIPM') as class_time,
		   |
		  |
		  |jp_class_pkg.get_session_notes(i.instance_id) as notes,
		   |
		  |(case when nvl(t.no_limit,'N') = 'Y' then
		   |  '<i>No Limit</i>' else '<b>'||
		   |  (case jp_class_pkg.spots_left(i.instance_id,$reserveEligible) when 0 then
		   |    (case when jp_class_pkg.next_stagger(i.instance_id) <> 0 then
		   |      (case when jp_class_pkg.has_prev_stagger(i.instance_id) = 'Y' then
		   |        '<span style="color:#F80;">FULL*</span><br>More Spots Opening:<br>'||
		   |        (select to_char(stagger_date,'MM/DD/YYYY')||'<br>'||trim(leading '0' from to_char(stagger_date,'HH:MIPM')) from jp_class_staggers where stagger_id = jp_class_pkg.next_stagger(i.instance_Id))
		   |      else
		   |        '<span style="color:#77D;">CLASS OPENING</span><br>'||
		   |        (select to_char(stagger_date,'MM/DD/YYYY')||'<br>'||trim(leading '0' from to_char(stagger_date,'HH:MIPM')) from jp_class_staggers where stagger_id = jp_class_pkg.next_stagger(i.instance_Id))
		   |      end)
		   |    else
		   |      '<span style="color:red;">FULL</span><br>('||
		   |      jp_class_pkg.number_waiting(i.instance_id)||'&nbsp;Waiting)'
		   |    end) ||
		   |    (case when $reserveEligible = 'N' and jp_class_pkg.spots_left(i.instance_id,'Y') > 0 then
		   |    '<br><span style="color:#2358A6; cursor:help;" onMouseover="ddrivetip(tooltipText(''R''),''lightYellow'',300); resizeRatings();"
		   |     onMouseout="hideddrivetip()">(' || jp_class_pkg.spots_left(i.instance_id,'Y') || '&nbsp;Reserved)</span>'
		   |    end)
		   |  else
		   |    ''||jp_class_pkg.spots_left(i.instance_id,$reserveEligible)
		   |  end)
		   |  ||'</b>'
		   |end)   as spots_left,
		   |
		  |(case when exists(select 1 from jp_class_signups where person_id = ${juniorId.getOrElse("null")} and instance_id = i.instance_id and signup_type = 'E') then
		   |  'Unenroll'
		   |
		  |  when exists(select 1 from jp_class_signups where person_id = ${juniorId.getOrElse("null")} and instance_id = i.instance_id and signup_type = 'W') then
		   |  'Delist'
		   |
		  |else
		   |  (case when (s1.session_datetime - 2/24) < util_pkg.get_sysdate then
		   |    'Begun'
		   |  else
		   |    (case when jp_class_pkg.spots_left(i.instance_id,$reserveEligible) > 0 then
		   |      'Enroll'
		   |    else
		   |      (case when jp_class_pkg.wl_exists(i.instance_id) = 'Y' then
		   |        'Wait List'
		   |      else
		   |        'Not Available'
		   |      end)
		   |    end)
		   |  end)
		   |end) as action
		   |from jp_class_types t, jp_class_instances i, jp_class_sessions s1, jp_class_Sessions s2, jp_class_bookends bk
		   |where i.type_id = t.type_id
		   |and bk.instance_id = i.instance_id and s1.session_id = bk.first_session and s2.session_id = bk.last_session
		   |and (nvl($weekString, '%nu'||'ll%') = '%nu' || 'll%' or (
		   |    s1.session_datetime between (select monday from jp_weeks where season = util_pkg.get_current_season and week = $weekString)
		   |    and (select sunday from jp_weeks where season = util_pkg.get_current_season and week = $weekString)
		   |))
		   |and to_char(s1.session_datetime,'YYYY') = util_pkg.get_current_season
		   |and ${juniorId.map(j => s"jp_class_pkg.see_instance($j,i.instance_id)").getOrElse("'Y'")}  = 'Y'
		   |and t.type_id = $typeId
		   |and nvl(i.price,0) = 0
		   |and nvl(i.reserved_for_group,'N') <> 'Y'
		   |order by s1.session_datetime
		""".stripMargin
	}

}

case class GetClassInstancesQueryResult(
	instanceId: Int,
	className: String,
	firstDay: String,
	lastDay: String,
	classTime: String,
	notes: String,
	spotsLeft: String,
	action: String
)

object GetClassInstancesQueryResult {
	implicit val format = Json.format[GetClassInstancesQueryResult]
}