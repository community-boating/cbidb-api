package org.sailcbi.APIServer.IO.PreparedQueries.Public

import com.coleji.framework.IO.PreparedQueries.HardcodedQueryForSelectCastableToJSObject
import com.coleji.framework.Storable.ResultSetWrapper
import com.coleji.framework.Util.DateUtil
import org.sailcbi.APIServer.UserTypes.PublicRequestCache
import play.api.libs.json.{JsArray, JsNumber, JsString}

import java.time.LocalDateTime

class GetFlagColorHistory extends HardcodedQueryForSelectCastableToJSObject[GetFlagColorHistoryResult](Set(PublicRequestCache)) {
	val getQuery: String =
		"""
		  |select flag, change_datetime from flag_changes where change_datetime >= (util_pkg.get_sysdate - 2) order by change_datetime desc
		""".stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): GetFlagColorHistoryResult = GetFlagColorHistoryResult(
		rs.getString(1),
		rs.getLocalDateTime(2),
	)

	val columnNames = List(
		"FLAG_COLOR",
		"CHANGE_DATETIME"
	)

	def mapCaseObjectToJsArray(caseObject: GetFlagColorHistoryResult): JsArray = JsArray(IndexedSeq(
		JsString(caseObject.color),
		JsNumber(DateUtil.toBostonTime(caseObject.changeDatetime).toEpochSecond),
	))
}

case class GetFlagColorHistoryResult(color: String, changeDatetime: LocalDateTime)