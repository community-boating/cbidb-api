package org.sailcbi.APIServer.IO.PreparedQueries.Public

import java.sql.ResultSet
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import org.sailcbi.APIServer.IO.PreparedQueries.HardcodedQueryForSelectCastableToJSObject
import org.sailcbi.APIServer.Services.Authentication.PublicUserType
import play.api.libs.json.{JsArray, Json}

class GetApClassInstances(startDate: LocalDate) extends HardcodedQueryForSelectCastableToJSObject[GetApClassInstancesResult](Set(PublicUserType)) {
	val startDateString: String = startDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
	val getQuery: String =
		s"""
		   |select i.instance_id, t.type_name, to_char(fs.session_datetime, 'MM/DD/YYYY') as start_date, to_char(fs.session_datetime, 'HH:MIPM') as start_time, i.location_string,
		   |(select count(*) from ap_class_signups where instance_id = i.instance_id and signup_type = 'E') as enrollees
		   |from ap_class_types t, ap_class_formats f, ap_class_instances i, ap_class_bookends bk, ap_class_sessions fs
		   |where i.instance_id = bk.instance_id and bk.first_session = fs.session_id
		   |and i.format_id = f.format_id and f.type_id = t.type_id
		   |and trunc(fs.session_datetime) = to_date('$startDateString','MM/DD/YYYY')
		   |and i.cancelled_datetime is null
		   |and nvl(i.hide_online,'N') <> 'Y'
		   |order by fs.session_datetime
		   |
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
		Json.toJson(caseObject.instanceId),
		Json.toJson(caseObject.typeName),
		Json.toJson(caseObject.sessionDate),
		Json.toJson(caseObject.sessionTime),
		Json.toJson(caseObject.location),
		Json.toJson(caseObject.enrollees)
	))
}

case class GetApClassInstancesResult(
	instanceId: Int,
	typeName: String,
	sessionDate: String,
	sessionTime: String,
	location: String,
	enrollees: Int
)