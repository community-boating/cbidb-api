package Reporting

import Entities.ApClassInstance
import Reporting.ReportFactories.ReportFactoryApClassInstance
import Reporting.ReportingFields.ReportingField
import Reporting.ReportingFilters.{ApClassInstanceFilterType, ApClassInstanceFilterYear, ReportingFilter, ReportingFilterSpecParser}
import Services.PersistenceBroker
import Storable.StorableClass

class Report[T <: StorableClass](instances: Set[T], fields: List[ReportingField[T]]) {
/*
  val instancesList: List[T] = instances.toList

  def getReport(pb: PersistenceBroker): String = {
    val valueFunctions: List[ValueFunction] = fields.map(f => f.getValueFunction(pb, instancesList))

    fields.map(f => f.fieldDisplayName).mkString("\t") +
    "\n" +
    instancesList.map(i => {
      valueFunctions.map(fn => {
        fn(i)
      }).mkString("\t")
    }).mkString("\n")
  }*/
}

object Report {
  type StorableClassType = Class[_ <: StorableClass]
  type ReportingFilterType = Class[_ <: ReportingFilter[_]]

  val BASE_ENTITY_MAP: Map[String, PersistenceBroker => ReportingFilterSpecParser[_ <: StorableClass]] = Map(
    "ApClassInstance" -> ((pb: PersistenceBroker) => new ReportingFilterSpecParser[ApClassInstance](pb))
  )

  val FILTER_MAP: Map[String, ReportingFilterType] = Map(
    "ApClassInstanceFilterType" -> classOf[ApClassInstanceFilterType],
    "ApClassInstanceFilterYear" -> classOf[ApClassInstanceFilterYear]
  )

  def getReport(pb: PersistenceBroker, baseEntityName: String, filterSpec: String, fieldSpec: String): String = {
    val factory = baseEntityName match {
      case "ApClassInstance" => new ReportFactoryApClassInstance(pb, filterSpec, fieldSpec)
    }
    factory.getReportText
  }

  class BadReportingBaseEntityException(
    private val message: String = "",
    private val cause: Throwable = None.orNull
  ) extends Exception(message, cause)
}
