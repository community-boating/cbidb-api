package org.sailcbi.APIServer.IO.PreparedQueries.Staff

import com.coleji.framework.IO.PreparedQueries.HardcodedQueryForSelectCastableToJSObject
import com.coleji.framework.Storable.ResultSetWrapper
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsArray, JsString}

class GetUsers extends HardcodedQueryForSelectCastableToJSObject[GetUsersResult](Set(StaffRequestCache)) {
	val getQuery: String =
		"""
		  |select user_id, user_name, name_first, name_last, email, active, hide_from_close from users
		""".stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): GetUsersResult = GetUsersResult(
		rs.getInt(1),
		rs.getString(2),
		rs.getString(3),
		rs.getString(4),
		rs.getString(5),
		rs.getString(6) == "Y",
		rs.getString(7) == "Y",
	)

	val columnNames = List(
		"USER_ID",
		"USER_NAME",
		"NAME_FIRST",
		"NAME_LAST",
		"EMAIL",
		"ACTIVE",
		"HIDE_FROM_CLOSE"
	)

	def mapCaseObjectToJsArray(caseObject: GetUsersResult): JsArray = JsArray(IndexedSeq(
		JsString(caseObject.userId.toString),
		JsString(caseObject.userName),
		JsString(caseObject.nameFirst),
		JsString(caseObject.nameLast),
		JsString(caseObject.email),
		JsString(if (caseObject.active) "Y" else "N"),
		JsString(if (caseObject.hideFromClose) "Y" else "N")
	))
}

case class GetUsersResult(
	userId: Int,
	userName: String,
	nameFirst: String,
	nameLast: String,
	email: String,
	active: Boolean,
	hideFromClose: Boolean
)
