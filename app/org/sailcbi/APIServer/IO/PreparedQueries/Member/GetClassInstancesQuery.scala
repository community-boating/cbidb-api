package org.sailcbi.APIServer.IO.PreparedQueries.Member

import com.coleji.neptune.IO.PreparedQueries.{HardcodedQueryForSelect, HardcodedQueryForSelectCastableToJSObject}
import com.coleji.neptune.Storable.ResultSetWrapper
import org.sailcbi.APIServer.UserTypes.{MemberRequestCache, PublicRequestCache, StaffRequestCache}
import play.api.libs.json.{JsArray, Json}

object GetClassInstancesQuery {
	def byJunior(week: Option[Int], typeId: Int, juniorId: Int): HardcodedQueryForSelect[GetClassInstancesQueryResult] = {
		new HardcodedQueryForSelect[GetClassInstancesQueryResult](Set(MemberRequestCache)) {
			override def getQuery: String = query(week, Some(typeId), Some(juniorId))

			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): GetClassInstancesQueryResult = mapResultSetRowToCaseObjectImpl(rs)
		}
	}

	def public(): HardcodedQueryForSelectCastableToJSObject[GetClassInstancesQueryResult] = {
		new HardcodedQueryForSelectCastableToJSObject[GetClassInstancesQueryResult](Set(StaffRequestCache, PublicRequestCache)) {
			override def getQuery: String = query(None, None, None)

			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): GetClassInstancesQueryResult = mapResultSetRowToCaseObjectImpl(rs)

			override val columnNames: List[String] = List(
				"INSTANCE_ID",
				"CLASS_NAME",
				"FIRST_DAY",
				"LAST_DAY",
				"CLASS_TIME",
				"NOTES",
				"SPOTS_LEFT",
				"ACTION",
				"TYPE_ID",
				"START_DATETIME_RAW",
				"END_DATETIME_RAW",
				"WEEK"
			)

			override def mapCaseObjectToJsArray(o: GetClassInstancesQueryResult): JsArray = JsArray(IndexedSeq(
				Json.toJson(o.instanceId),
				Json.toJson(o.className),
				Json.toJson(o.firstDay),
				Json.toJson(o.lastDay),
				Json.toJson(o.classTime),
				Json.toJson(o.notes),
				Json.toJson(o.spotsLeft),
				Json.toJson(o.action),
				Json.toJson(o.typeId),
				Json.toJson(o.startDatetimeRaw),
				Json.toJson(o.endDatetimeRaw),
				Json.toJson(o.week)
			))
		}
	}

	def mapResultSetRowToCaseObjectImpl(rs: ResultSetWrapper): GetClassInstancesQueryResult = GetClassInstancesQueryResult(
		rs.getInt(1),
		rs.getString(2),
		rs.getString(3),
		rs.getString(4),
		rs.getString(5),
		rs.getString(6),
		rs.getString(13), // rs.getString(7), // new spotsleft text
		rs.getString(8),
		rs.getInt(9),
		rs.getString(10),
		rs.getString(11),
		rs.getInt(12)
	)

	def query(week: Option[Int], typeId: Option[Int], juniorId: Option[Int]): String = {
		val reserveEligible = juniorId match {
			case Some(j) => s"jp_class_pkg.is_reserve_eligible(i.instance_id,${j})"
			case None => "'N'"
		}

		val weekString = week.getOrElse("null")

		val typeIdClause = typeId match {
			case Some(t) => s"and t.type_id = $typeId"
			case None => ""
		}

		val weekClause = week match {
			case Some(w) => s"and week = $weekString"
			case None => ""
		}

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
		   |end) as action,
		   |i.type_id,
		   |to_char(s1.session_datetime,'MM/DD/YYYY HH24:MI') as start_datetime_raw,
		   |to_char(s2.session_datetime,'MM/DD/YYYY HH24:MI') as end_datetime_raw,
		   |w.week,
		   |jp_class_pkg.class_status_text(i.instance_id)
		   |from jp_class_types t, jp_class_instances i, jp_class_sessions s1, jp_class_Sessions s2, jp_class_bookends bk, jp_weeks w
		   |where i.type_id = t.type_id
		   |and (select count(*) from jp_class_staggers where instance_id = i.instance_id) > 0
		   |and bk.instance_id = i.instance_id and s1.session_id = bk.first_session and s2.session_id = bk.last_session
		   |and s1.session_datetime between w.monday and w.sunday
		   |and (nvl($weekString, '%nu'||'ll%') = '%nu' || 'll%' or (
		   |    s1.session_datetime between (select monday from jp_weeks where season = util_pkg.get_current_season $weekClause)
		   |    and (select sunday from jp_weeks where season = util_pkg.get_current_season $weekClause)
		   |))
		   |and to_char(s1.session_datetime,'YYYY') = util_pkg.get_current_season
		   |and ${juniorId.map(j => s"jp_class_pkg.see_instance($j,i.instance_id)").getOrElse("'Y'")}  = 'Y'
		   |$typeIdClause
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
	action: String,
	typeId: Int,
	startDatetimeRaw: String,
	endDatetimeRaw: String,
	week: Int
)

object GetClassInstancesQueryResult {
	implicit val format = Json.format[GetClassInstancesQueryResult]
}