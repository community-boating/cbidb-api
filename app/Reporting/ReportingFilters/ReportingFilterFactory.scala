package Reporting.ReportingFilters

import Services.PersistenceBroker
import Storable.StorableClass

abstract class ReportingFilterFactory[T <: StorableClass] {
  val displayName: String
  def getFilter(pb: PersistenceBroker, args: String): ReportingFilter[T]
  val defaultValue: String
  val argTypes: List[ReportingFilterArgType]
}

object ReportingFilterFactory {


  class BadReportingFilterFactoryArgumentException(
    private val message: String = "",
    private val cause: Throwable = None.orNull
  ) extends Exception(message, cause)
}

