package Reporting.ReportingFields

import Services.PersistenceBroker
import Storable.Fields.FieldValue.{IntFieldValue, NullableIntFieldValue, NullableStringFieldValue, StringFieldValue}
import Storable.Fields._
import Storable.StorableClass

trait ReportingField[T] {
  def getValueFunction(pb: PersistenceBroker, instances: List[T]): T => String
}

object ReportingField {
  def getReportingFieldFromDatabaseField[T <: StorableClass](f: DatabaseField[_]): ReportingField[T] =
    getReportingFieldFromDatabaseFieldParentObject[T, T](f, t => t)

  def getReportingFieldFromDatabaseFieldParentObject[T <: StorableClass, U <: StorableClass](
    f: DatabaseField[_],
    getParent: (T => U)
  ): ReportingField[T] = f match {
    case i: IntDatabaseField => new CustomReportingField[T]((t: T) => {
      getParent(t).intValueMap.get(i.getRuntimeFieldName) match {
        case Some(v: IntFieldValue) => v.get.toString
        case None => ""
      }
    })
    case i: NullableIntDatabaseField => new CustomReportingField[T]((t: T) => {
      getParent(t).nullableIntValueMap.get(i.getRuntimeFieldName) match {
        case Some(v: NullableIntFieldValue) => v.get.toString
        case None => ""
      }
    })
    case i: StringDatabaseField => new CustomReportingField[T]((t: T) => {
      getParent(t).stringValueMap.get(i.getRuntimeFieldName) match {
        case Some(v: StringFieldValue) => v.get.toString
        case None => ""
      }
    })
    case i: NullableStringDatabaseField => new CustomReportingField[T]((t: T) => {
      getParent(t).nullableStringValueMap.get(i.getRuntimeFieldName) match {
        case Some(v: NullableStringFieldValue) => v.get.toString
        case None => ""
      }
    })
  }
}