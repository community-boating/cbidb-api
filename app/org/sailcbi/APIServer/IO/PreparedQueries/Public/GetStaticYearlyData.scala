package org.sailcbi.APIServer.IO.PreparedQueries.Public

import com.coleji.framework.IO.PreparedQueries.HardcodedQueryForSelectCastableToJSObject
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.Services.ResultSetWrapper
import org.sailcbi.APIServer.UserTypes.PublicRequestCache
import play.api.libs.json.{JsArray, JsNumber, JsString}

class GetStaticYearlyData extends HardcodedQueryForSelectCastableToJSObject[GetStaticYearlyDataResult](Set(PublicRequestCache)) {
	val getQuery: String =
		s"""
		  |select
		  |price,
		  |util_pkg.get_current_season,
		  |check_yearly_date('JP_REGISTER', util_pkg.get_sysdate, util_pkg.get_current_season)
		  |from membership_types where membership_type_id = ${MagicIds.JUNIOR_SUMMER_MEMBERSHIP_TYPE_ID}
		""".stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): GetStaticYearlyDataResult = GetStaticYearlyDataResult(
		rs.getInt(1),
		rs.getInt(2),
		rs.getString(3)
	)

	val columnNames = List(
		"PRICE",
		"CURRENT_SEASON",
		"JP_REGISTER"
	)

	def mapCaseObjectToJsArray(caseObject: GetStaticYearlyDataResult): JsArray = JsArray(IndexedSeq(
		JsNumber(caseObject.jpPrice),
		JsNumber(caseObject.currentSeason),
		JsString(caseObject.jpRegister)
	))
}

case class GetStaticYearlyDataResult(jpPrice: Int, currentSeason: Int, jpRegister: String)