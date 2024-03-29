package org.sailcbi.APIServer.IO.PreparedQueries.Public

import com.coleji.neptune.IO.PreparedQueries.HardcodedQueryForSelectCastableToJSObject
import com.coleji.neptune.Storable.ResultSetWrapper
import org.sailcbi.APIServer.UserTypes.PublicRequestCache
import play.api.libs.json.{JsArray, JsString}

class GetFlagColor extends HardcodedQueryForSelectCastableToJSObject[GetFlagColorResult](Set(PublicRequestCache)) {
	val getQuery: String =
		"""
		  |select flag from flag_changes where change_datetime = (select max(change_datetime) from flag_changes)
		""".stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): GetFlagColorResult = GetFlagColorResult(
		rs.getString(1),
	)

	val columnNames = List(
		"FLAG_COLOR",
	)

	def mapCaseObjectToJsArray(caseObject: GetFlagColorResult): JsArray = JsArray(IndexedSeq(
		JsString(caseObject.color),
	))
}

case class GetFlagColorResult(color: String)