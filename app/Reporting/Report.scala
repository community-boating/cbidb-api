package Reporting

import Reporting.ReportFactories.{ReportFactoryApClassInstance, ReportFactoryJpClassInstance}
import Services.PersistenceBroker

case class Report(baseEntityName: String, filterSpec: String, fieldSpec: String) {
  def getReport(pb: PersistenceBroker): String = {
    val factory = baseEntityName match {
      case "ApClassInstance" => new ReportFactoryApClassInstance(pb, filterSpec, fieldSpec)
      case "JpClassInstance" => new ReportFactoryJpClassInstance(pb, filterSpec, fieldSpec)
    }
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