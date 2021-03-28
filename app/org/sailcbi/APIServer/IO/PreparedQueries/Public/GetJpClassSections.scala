package org.sailcbi.APIServer.IO.PreparedQueries.Public

import com.coleji.framework.IO.PreparedQueries.HardcodedQueryForSelectCastableToJSObject
import org.sailcbi.APIServer.Services.ResultSetWrapper
import org.sailcbi.APIServer.UserTypes.PublicRequestCache
import play.api.libs.json.{JsArray, Json}

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GetJpClassSections(startDate: LocalDate) extends HardcodedQueryForSelectCastableToJSObject[GetJpClassSectionsResult](Set(PublicRequestCache)) {
	val startDateString: String = startDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
	val getQuery: String = s"""
		|select
		|ilv.section_id as id,
		|sl.section_name,
		|ilv.instance_id as instance_id,
		|t.type_name,
		|to_char(s.session_datetime, 'MM/DD/YYYY') as start_date,
		|to_char(s.session_datetime, 'HH:MIPM') as start_time,
		|l.location_name,
		|ins.name_first as instructor_name_first,
		|ins.name_last as instructor_name_last,
		|(select count(*) from jp_class_signups where instance_id = ilv.instance_id and signup_type = 'E') as enrollees
		|from jp_class_types t, jp_class_sessions s,
		|class_locations l, class_instructors ins, jp_class_section_lookup sl, (
		|    select i.instance_id, i.type_id, loc1.location_id, ins1.instructor_id, nvl(se.section_id, -1 * i.instance_id) as section_id, se.lookup_id
		|    from jp_class_instances i
		|    left outer join jp_class_signups si
		|    on si.instance_id = i.instance_id and si.signup_type = 'E'
		|    left outer join jp_class_sections se
		|    on se.instance_id = i.instance_id
		|    and si.section_id = se.section_id
		|    left outer join class_instructors ins1
		|    on ins1.instructor_id = nvl2(se.section_id, se.instructor_id, i.instructor_id)
		|    left outer join class_locations loc1
		|    on loc1.location_id = nvl2(se.section_id, se.location_id, i.location_id)
		|    group by i.instance_id, i.type_id, loc1.location_id, ins1.instructor_id, nvl(se.section_id, -1 * i.instance_id), se.lookup_id
		|) ilv
		|where ilv.type_id = t.type_id and s.instance_id = ilv.instance_id
		|and ilv.location_id = l.location_id (+) and ilv.instructor_id = ins.instructor_id (+)
		|and ilv.lookup_id = sl.section_id (+)
		|and to_date('$startDateString', 'MM/DD/YYYY') = trunc(s.session_datetime)
		|order by s.session_datetime, t.display_order, ilv.instance_id
		|
	""".stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): GetJpClassSectionsResult = GetJpClassSectionsResult(
		rs.getInt(1),
		rs.getOptionString(2),
		rs.getInt(3),
		rs.getString(4),
		rs.getString(5),
		rs.getString(6),
		rs.getString(7),
		rs.getString(8),
		rs.getString(9),
		rs.getInt(10)
	)

	val columnNames = List(
		"SECTION_ID",
		"SECTION_NAME",
		"INSTANCE_ID",
		"TYPE_NAME",
		"START_DATE",
		"START_TIME",
		"LOCATION_NAME",
		"INSTRUCTOR_NAME_FIRST",
		"INSTRUCTOR_NAME_LAST",
		"ENROLLEES"
	)

	def mapCaseObjectToJsArray(caseObject: GetJpClassSectionsResult): JsArray = JsArray(IndexedSeq(
		Json.toJson(caseObject.sectionId),
		Json.toJson(caseObject.sectionName.orNull),
		Json.toJson(caseObject.instanceId),
		Json.toJson(caseObject.typeName),
		Json.toJson(caseObject.sessionDate),
		Json.toJson(caseObject.sessionTime),
		Json.toJson(caseObject.location),
		Json.toJson(caseObject.instructorFirstName),
		Json.toJson(caseObject.instructorLastName),
		Json.toJson(caseObject.enrollees)
	))

}

case class GetJpClassSectionsResult(
	sectionId: Int,
	sectionName: Option[String],
	instanceId: Int,
	typeName: String,
	sessionDate: String,
	sessionTime: String,
	location: String,
	instructorFirstName: String,
	instructorLastName: String,
	enrollees: Int
)

object GetJpClassSectionsResult {
	implicit val format = Json.format[GetJpClassSectionsResult]
}