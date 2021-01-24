package org.sailcbi.APIServer.IO.PreparedQueries.Member

import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.MemberRequestCache
import org.sailcbi.APIServer.Services.ResultSetWrapper
import play.api.libs.json.Json

class JpOffseasonClasses(juniorId: Int) extends PreparedQueryForSelect[JpOffseasonClassesResult](Set(MemberRequestCache)) {
	override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): JpOffseasonClassesResult = JpOffseasonClassesResult(
		className=rsw.getString(1),
		typeId = rsw.getInt(2),
		instanceId = rsw.getInt(3),
		price=rsw.getDouble(4),
		timeRange = rsw.getString(5),
		classDates = rsw.getString(6),
		seeType = rsw.getString(7),
		spotsLeft = rsw.getInt(8),
		signupCtThisJunior = rsw.getInt(9)
	)

	override val params: List[String] = List(juniorId.toString, juniorId.toString)

	override def getQuery: String = """
	  |select
	  |nvl(i.name_override,t.type_name) as class_name,
	  |t.type_id,
	  |i.instance_id,
	  |nvl(i.price,0) as price,
	  |trim(leading '0' from to_char(fs.session_datetime,'HH:MIPM'))||' - '||to_char(fs.session_datetime + nvl(fs.length_override,t.session_length)/24,'HH:MIPM') as time_range,
	  |ilv.class_dates,
	  |jp_class_pkg.see_type(?, i.type_id),
	  |jp_class_pkg.spots_left(i.instance_Id),
	  |(select count(*) from jp_class_signups where person_id = ? and instance_id = i.instance_id and signup_type = 'E')
	  |from jp_class_types t, jp_class_instances i, jp_class_bookends bk, jp_class_sessions fs, jp_weeks w, (
	  |    select instance_id, listagg(trim(leading '0' from to_char(session_datetime,'MM/DD')), ', ') within group (order by session_datetime) as class_dates
	  |    from jp_class_sessions group by instance_id
	  |) ilv
	  |where t.type_id = i.type_id and i.instance_id = bk.instance_id and bk.first_session = fs.session_id and fs.session_datetime between w.monday and w.sunday and w.week in (0) and w.season = util_pkg.get_current_season
	  |and i.instance_id = ilv.instance_id
	  |and nvl(i.price,0) > 0
	  |order by t.display_order
	|""".stripMargin
}

case class JpOffseasonClassesResult(
	className: String,
	typeId: Int,
	instanceId: Int,
	price: Double,
	timeRange: String,
	classDates: String,
	seeType: String,
	spotsLeft: Int,
	signupCtThisJunior: Int
)

object JpOffseasonClassesResult {
	implicit val format = Json.format[JpOffseasonClassesResult]
}