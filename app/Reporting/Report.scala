package Reporting

import Reporting.ReportFactories._
import Services.RequestCache
import Storable.StorableClass
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
	val reportFactoryMap: Map[String, (String, Class[_ <: ReportFactory[_]])] = Map(
		"ApClassInstance" -> ("AP Class Instance", classOf[ReportFactoryApClassInstance]),
		"JpClassInstance" -> ("JP Class Instance", classOf[ReportFactoryJpClassInstance]),
		"JpClassSignup" -> ("JP Class Signup", classOf[ReportFactoryJpClassSignup]),
		"Person" -> ("Person", classOf[ReportFactoryPerson]),
		"Donation" -> ("Donation", classOf[ReportFactoryDonation])
	)

	def getReport(rc: RequestCache, baseEntityName: String, filterSpec: String, fieldSpec: String): Report = {
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