package Reporting.ReportingFilters.ReportingFilterFactories

import Reporting.ReportingFilters.ReportingFilter
import Services.PersistenceBroker
import Storable.StorableClass

sealed abstract class ReportingFilterFactory[T <: StorableClass, U] {
  val displayName: String
  def getFilterCastArg(pb: PersistenceBroker, arg: U): ReportingFilter[T]
  def getFilter(pb: PersistenceBroker, args: String): ReportingFilter[T]
}

trait ReportingFilterFactoryDropdown {
  def getDropdownValues(pb: PersistenceBroker): List[(String, String)]
}

abstract class ReportingFilterFactoryInt[T <: StorableClass] extends ReportingFilterFactory[T, Int] {
  def getFilterCastArg(pb: PersistenceBroker, arg: Int): ReportingFilter[T]
  final def getFilter(pb: PersistenceBroker, args: String): ReportingFilter[T] = {
    try {
      val arg: Int = args.toInt
      getFilterCastArg(pb, arg)
    } catch {
      case _: Throwable => throw new BadReportingFilterFactoryArgumentException
    }
  }
}

class BadReportingFilterFactoryArgumentException(
  private val message: String = "",
  private val cause: Throwable = None.orNull
) extends Exception(message, cause)