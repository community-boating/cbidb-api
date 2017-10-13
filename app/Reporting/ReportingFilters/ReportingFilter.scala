package Reporting.ReportingFilters

import Storable.StorableClass

abstract class ReportingFilter[T <: StorableClass] {
  val instances: Set[T]

  def and(other: ReportingFilter[T]) = new CompositeReportingFilter[T](this.instances.intersect(other.instances))

  def or(other: ReportingFilter[T]) = new CompositeReportingFilter[T](this.instances.union(other.instances))

  class CompositeReportingFilter[T <: StorableClass](val instances: Set[T]) extends ReportingFilter[T]

  class BadReportingFilterArgumentsException(
    private val message: String = "",
    private val cause: Throwable = None.orNull
  ) extends Exception(message, cause)
}
