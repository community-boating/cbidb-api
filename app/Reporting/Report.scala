package Reporting

import Entities.ApClassInstance
import Reporting.ReportingFields.ReportingField
import Reporting.ReportingFilters.{ApClassInstanceFilterType, ApClassInstanceFilterYear, ReportingFilter}
import Services.PersistenceBroker
import Storable.{StorableClass, StorableObject}
import ReportingFields._

class Report[T <: StorableClass](instances: Set[T], fields: List[ReportingField[T]]) {
  type ValueFunction = (T => String)
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
  }
}

object Report {
  type StorableClassType = Class[_ <: StorableClass]
  type ReportingFilterType = Class[_ <: ReportingFilter[_]]

  val BASE_ENTITY_MAP: Map[String, StorableClassType] = Map(
    "ApClassInstance" -> classOf[ApClassInstance]
  )

  val FILTER_MAP: Map[String, ReportingFilterType] = Map(
    "ApClassInstanceFilterType" -> classOf[ApClassInstanceFilterType],
    "ApClassInstanceFilterYear" -> classOf[ApClassInstanceFilterYear]
  )

  def getBaseEntity(entityName: String): StorableClassType = {
    BASE_ENTITY_MAP.get(entityName) match {
      case Some(o: StorableClassType) => o
      case None => throw new BadReportingBaseEntityException("No such reporting base entity " + entityName)
    }
  }

  class BadReportingBaseEntityException(
    private val message: String = "",
    private val cause: Throwable = None.orNull
  ) extends Exception(message, cause)
}
