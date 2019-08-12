package IO.PreparedQueries.Public

import java.sql.ResultSet
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import IO.PreparedQueries.HardcodedQueryForSelectCastableToJSObject
import Services.Authentication.PublicUserType
import play.api.libs.json.{JsArray, JsString, Json}

class GetJpClassInstances(startDate: LocalDate) extends HardcodedQueryForSelectCastableToJSObject[GetJpClassInstancesResult](Set(PublicUserType)) {
	val startDateString: String = startDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
	val getQuery: String =
		s"""
		   |select i.instance_id, t.type_name,
		   |to_char(s.session_datetime, 'MM/DD/YYYY') as start_date,
		   |to_char(s.session_datetime, 'HH:MIPM') as start_time,
		   |l.location_name,
		   |ins.name_first as instructor_name_first,
		   |ins.name_last as instructor_name_last,
		   |(select count(*) from jp_class_signups where instance_id = i.instance_id and signup_type = 'E') as enrollees
		   |from jp_class_types t, jp_class_instances i, jp_class_sessions s,
		   |class_locations l, class_instructors ins
		   |where i.instance_id = s.instance_id
		   |and i.type_id = t.type_id
		   |and i.location_id = l.location_id (+)
		   |and i.instructor_id = ins.instructor_id (+)
		   |and trunc(s.session_datetime) = to_date('$startDateString', 'MM/DD/YYYY')
		   |order by s.session_datetime, t.display_order, i.instance_id
		   |
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSet): GetJpClassInstancesResult = GetJpClassInstancesResult(
		rs.getInt(1),
		rs.getString(2),
		rs.getString(3),
		rs.getString(4),
		rs.getString(5),
		rs.getString(6),
		rs.getString(7),
		rs.getInt(8)
	)

	val columnNames = List(
		"INSTANCE_ID",
		"TYPE_NAME",
		"START_DATE",
		"START_TIME",
		"LOCATION_NAME",
		"INSTRUCTOR_NAME_FIRST",
		"INSTRUCTOR_NAME_LAST",
		"ENROLLEES"
	)

	def mapCaseObjectToJsArray(caseObject: GetJpClassInstancesResult): JsArray = JsArray(IndexedSeq(
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

case class GetJpClassInstancesResult(
	instanceId: Int,
	typeName: String,
	sessionDate: String,
	sessionTime: String,
	location: String,
	instructorFirstName: String,
	instructorLastName: String,
	enrollees: Int
)

object GetJpClassInstancesResult {
	implicit val format = Json.format[GetJpClassInstancesResult]
}