package Reporting.ReportingFilters

import Storable.StorableClass

abstract class ReportingFilter[T <: StorableClass] {
  val primaryKeyValues: Set[Int]

  def and(other: ReportingFilter[T]) = new CompositeReportingFilter[T](this.primaryKeyValues.intersect(other.primaryKeyValues))

  def or(other: ReportingFilter[T]) = new CompositeReportingFilter[T](this.primaryKeyValues.union(other.primaryKeyValues))

  class CompositeReportingFilter[T <: StorableClass](val primaryKeyValues: Set[Int]) extends ReportingFilter[T]

  class BadReportingFilterArgumentsException(
    private val message: String = "",
    private val cause: Throwable = None.orNull
  ) extends Exception(message, cause)
}
