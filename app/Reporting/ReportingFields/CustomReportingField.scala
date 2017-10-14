package Reporting.ReportingFields
import Services.PersistenceBroker

class CustomReportingField[T](fn: (T => String), fieldDisplayName: String) extends ReportingField[T](fieldDisplayName) {
  override def getValueFunction(pb: PersistenceBroker, instances: List[T]): T => String = fn
}
