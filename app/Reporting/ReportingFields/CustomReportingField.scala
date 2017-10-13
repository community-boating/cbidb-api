package Reporting.ReportingFields
import Services.PersistenceBroker

class CustomReportingField[T](fn: (T => String)) extends ReportingField[T] {
  override def getValueFunction(pb: PersistenceBroker, instances: List[T]): T => String = fn
}
