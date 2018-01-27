package Logic.PreparedQueries.Public

import java.sql.ResultSet
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import Logic.PreparedQueries.{CastableToJSObject, PreparedQuery, PreparedQueryCaseResult, PreparedQueryCastableToJSObject}
import Services.Authentication.{PublicUserType, UserType}
import play.api.libs.json.{JsArray, JsObject, JsString}

class GetApClassInstances(startDate: LocalDate) extends PreparedQueryCastableToJSObject[GetApClassInstancesResult] {
  override val allowedUserTypes: Set[UserType] = Set(PublicUserType)
  val startDateString: String = startDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
  val getQuery: String =
    s"""
       |select i.instance_id, t.type_name, to_char(se.session_datetime, 'MM/DD/YYYY') as start_date,
       |to_char(se.session_datetime, 'HH:MIPM') as start_time, i.location_string,
       |(select count(*) from ap_class_signups where instance_id = i.instance_id and signup_type = 'E') as enrollees
       |from ap_class_types t, ap_class_formats f, ap_class_instances i, ap_class_sessions se
       |where se.instance_id = i.instance_id
       |and i.format_id = f.format_id
       |and f.type_id = t.type_id
       |and trunc(se.session_datetime) = to_date('$startDateString','MM/DD/YYYY')
       |and i.cancelled_datetime is null
       |and nvl(i.hide_online,'N') <> 'Y'
       |order by se.session_datetime
    """.stripMargin

  override def mapResultSetRowToCaseObject(rs: ResultSet): GetApClassInstancesResult = GetApClassInstancesResult(
    rs.getInt(1),
    rs.getString(2),
    rs.getString(3),
    rs.getString(4),
    rs.getString(5),
    rs.getInt(6)
  )

  val columnNames = List(
    "INSTANCE_ID",
    "TYPE_NAME",
    "START_DATE",
    "START_TIME",
    "LOCATION_STRING",
    "ENROLLEES"
  )

  def mapCaseObjectToJsArray(caseObject: GetApClassInstancesResult): JsArray = JsArray(IndexedSeq(
    JsString(caseObject.instanceId.toString),
    JsString(caseObject.typeName),
    JsString(caseObject.sessionDate),
    JsString(caseObject.sessionTime),
    JsString(caseObject.location),
    JsString(caseObject.enrollees.toString)
  ))
}

case class GetApClassInstancesResult(
  instanceId: Int,
  typeName: String,
  sessionDate: String,
  sessionTime: String,
  location: String,
  enrollees: Int
) extends PreparedQueryCaseResult