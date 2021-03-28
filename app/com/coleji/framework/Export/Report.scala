package com.coleji.framework.Export

import com.coleji.framework.Core.UnlockedRequestCache
import com.coleji.framework.Storable.StorableClass
import play.api.libs.json.{JsArray, JsObject, JsString}

case class Report(headers: List[String], rows: List[List[String]]) {
	def formatTSV: String = {
		(headers :: rows).map(row => {
			row.mkString("\t")
		}).mkString("\n")
	}

	def formatJSCON: JsObject = {
		JsObject(Map(
			"rows" -> JsArray(rows.map(row => JsArray(row.map(s => JsString(s))))),
			"metaData" -> JsArray(headers.map(h => JsObject(Map("name" -> JsString(h)))))
		))
	}
}

object Report {
	type ReportFactoryMap = Map[String, (String, Class[_ <: ReportFactory[_]])]

	def getReport(reportFactoryMap: ReportFactoryMap)(rc: UnlockedRequestCache, baseEntityName: String, filterSpec: String, fieldSpec: String): Report = {
		throw new Exception("Many changes have been made to the core system since reporting was last tested; should be exhaustively tested again before use")

		val c: Class[_ <: ReportFactory[_]] = reportFactoryMap(baseEntityName)._2
		val factory: ReportFactory[_ <: StorableClass] =
			Class.forName(c.getCanonicalName).newInstance.asInstanceOf[ReportFactory[_ <: StorableClass]]
		factory.setParameters(rc, filterSpec, fieldSpec)
		Report(factory.getHeaders, factory.getRows)
	}

	class BadReportingBaseEntityException(
		private val message: String = "",
		private val cause: Throwable = None.orNull
	) extends Exception(message, cause)

	class BadReportingFilterArgumentsException(
		private val message: String = "",
		private val cause: Throwable = None.orNull
	) extends Exception(message, cause)

}