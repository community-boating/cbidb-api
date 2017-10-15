package Reporting.ReportingFilters

import Storable.StorableClass

abstract class ReportingFilter[T <: StorableClass] {
  val instances: Set[T]

  def and(other: ReportingFilter[T]) = new ReportingFilterStatic[T](this.instances.intersect(other.instances))

  def or(other: ReportingFilter[T]) = new ReportingFilterStatic[T](this.instances.union(other.instances))
}