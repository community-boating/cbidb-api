package Reporting

import Reporting.ReportingFields.ReportingField
import Services.PersistenceBroker

class Report[T <: ReportableStorableClass](instances: Set[T], fields: List[ReportingField[T]]) {
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