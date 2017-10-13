package Reporting

import Entities.{ApClassInstance, JpClassInstance}
import Reporting.ReportingFields.ReportingField
import Services.PersistenceBroker
import Storable.{StorableClass, StorableObject}

class Report[T <: StorableClass](instances: Set[T], fields: List[ReportingField[T]]) {
  type ValueFunction = (T => String)
  val instancesList: List[T] = instances.toList

  def getReport(pb: PersistenceBroker): String = {
    val valueFunctions: List[ValueFunction] = fields.map(f => f.getValueFunction(pb, instancesList))

    println("!! " + instancesList.length)
    println(" @@ " + valueFunctions.length)

    instancesList.map(i => {
      valueFunctions.map(fn => {
        fn(i)
      }).mkString("\t")
    }).mkString("\n")
  }
}

object Report {
  val PERMITTED_BASE_ENTITIES: Set[StorableObject[_]] = Set (
    JpClassInstance,
    ApClassInstance
  )
}