package org.sailcbi.APIServer.IO.PreparedQueries.Public

import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.IO.PreparedQueries.HardcodedQueryForSelectCastableToJSObject
import org.sailcbi.APIServer.Services.Authentication.PublicUserType
import org.sailcbi.APIServer.Services.ResultSetWrapper
import play.api.libs.json.{JsArray, JsNumber}

class GetStaticYearlyData extends HardcodedQueryForSelectCastableToJSObject[GetStaticYearlyDataResult](Set(PublicUserType)) {
	val getQuery: String =
		s"""
		  |select price, util_pkg.get_current_season from membership_types where membership_type_id = ${MagicIds.JUNIOR_SUMMER_MEMBERSHIP_TYPE_ID}
		""".stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): GetStaticYearlyDataResult = GetStaticYearlyDataResult(
		rs.getInt(1),
		rs.getInt(2)
	)

	val columnNames = List(
		"PRICE",
		"CURRENT_SEASON"
	)

	def mapCaseObjectToJsArray(caseObject: GetStaticYearlyDataResult): JsArray = JsArray(IndexedSeq(
		JsNumber(caseObject.jpPrice),
		JsNumber(caseObject.currentSeason)
	))
}

case class GetStaticYearlyDataResult(jpPrice: Int, currentSeason: Int)