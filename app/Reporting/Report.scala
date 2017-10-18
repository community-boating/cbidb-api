package Reporting

import Reporting.ReportFactories.{ReportFactoryApClassInstance, ReportFactoryJpClassInstance}
import Services.PersistenceBroker
import Storable.StorableClass
case class Report(baseEntityName: String, filterSpec: String, fieldSpec: String) {
  // Stupid intellij thinks this is invalid.  It's not.
  val reportFactoryMap: Map[String, Class[_ <: ReportFactory[_ <: StorableClass]]] = Map(
    "ApClassInstance" -> classOf[ReportFactoryApClassInstance],
    "JpClassInstance" -> classOf[ReportFactoryJpClassInstance]
  )

  def getReport(pb: PersistenceBroker): String = {
    val c: Class[_ <: ReportFactory[_ <: StorableClass]] = reportFactoryMap(baseEntityName)
    val factory: ReportFactory[_ <: StorableClass] =
      Class.forName(c.getCanonicalName).newInstance.asInstanceOf[ReportFactory[_ <: StorableClass]]
    factory.setParameters(pb, filterSpec, fieldSpec)
    factory.getReportText
  }
}

object Report {
  class BadReportingBaseEntityException(
   private val message: String = "",
   private val cause: Throwable = None.orNull
 ) extends Exception(message, cause)

  class BadReportingFilterArgumentsException(
    private val message: String = "",
    private val cause: Throwable = None.orNull
  ) extends Exception(message, cause)
}