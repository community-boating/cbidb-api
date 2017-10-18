package Reporting

import Reporting.ReportFactories.{ReportFactoryApClassInstance, ReportFactoryJpClassInstance}
import Services.PersistenceBroker
import Storable.StorableClass

object Report {
  // Stupid intellij thinks this is invalid.  It's not.
  val reportFactoryMap: Map[String, (String, Class[_ <: ReportFactory[_ <: StorableClass]])] = Map(
    "ApClassInstance" -> ("AP Class Instances", classOf[ReportFactoryApClassInstance]),
    "JpClassInstance" -> ("JP Class Instances", classOf[ReportFactoryJpClassInstance])
  )

  def getReport(pb: PersistenceBroker, baseEntityName: String, filterSpec: String, fieldSpec: String): String = {
    val c: Class[_ <: ReportFactory[_ <: StorableClass]] = reportFactoryMap(baseEntityName)._2
    val factory: ReportFactory[_ <: StorableClass] =
      Class.forName(c.getCanonicalName).newInstance.asInstanceOf[ReportFactory[_ <: StorableClass]]
    factory.setParameters(pb, filterSpec, fieldSpec)
    factory.getReportText
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