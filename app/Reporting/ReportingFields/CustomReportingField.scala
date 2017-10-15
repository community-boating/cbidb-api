package Reporting.ReportingFields
import Services.PersistenceBroker
import Storable.StorableClass

class CustomReportingField[T <: StorableClass](
  fn: ((PersistenceBroker, List[T]) => T => String),
  fieldDisplayName: String
) extends ReportingField[T](fieldDisplayName) {
  override def getValueFunction(pb: PersistenceBroker, instances: List[T]): T => String = fn(pb, instances)
}
