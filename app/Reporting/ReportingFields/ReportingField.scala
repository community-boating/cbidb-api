package Reporting.ReportingFields

import Services.PersistenceBroker
import Storable.Fields.FieldValue.{IntFieldValue, NullableIntFieldValue, NullableStringFieldValue, StringFieldValue}
import Storable.Fields._
import Storable.StorableClass

abstract class ReportingField[T <: StorableClass](val fieldDisplayName: String) {
  def getValueFunction(pb: PersistenceBroker, instances: List[T]): T => String
}

object ReportingField {
  def getReportingFieldFromDatabaseField[T <: StorableClass](f: DatabaseField[_], fieldDisplayName: String): ReportingField[T] =
    getReportingFieldFromDatabaseFieldParentObject[T, T](f, t => t, fieldDisplayName)

  def getReportingFieldFromDatabaseFieldParentObject[T <: StorableClass, U <: StorableClass](
    f: DatabaseField[_],
    getParent: (T => U),
    fieldDisplayName: String
  ): ReportingField[T] = f match {
    case i: IntDatabaseField => new CustomReportingField[T]((pb: PersistenceBroker, instances: List[T]) => (t: T) => {
      getParent(t).intValueMap.get(i.getRuntimeFieldName) match {
        case Some(v: IntFieldValue) => v.get.toString
        case None => ""
      }
    }, fieldDisplayName)
    case i: NullableIntDatabaseField => new CustomReportingField[T]((pb: PersistenceBroker, instances: List[T]) => (t: T) => {
      getParent(t).nullableIntValueMap.get(i.getRuntimeFieldName) match {
        case Some(v: NullableIntFieldValue) => v.get.toString
        case None => ""
      }
    }, fieldDisplayName)
    case i: StringDatabaseField => new CustomReportingField[T]((pb: PersistenceBroker, instances: List[T]) => (t: T) => {
      getParent(t).stringValueMap.get(i.getRuntimeFieldName) match {
        case Some(v: StringFieldValue) => v.get.toString
        case None => ""
      }
    }, fieldDisplayName)
    case i: NullableStringDatabaseField => new CustomReportingField[T]((pb: PersistenceBroker, instances: List[T]) => (t: T) => {
      getParent(t).nullableStringValueMap.get(i.getRuntimeFieldName) match {
        case Some(v: NullableStringFieldValue) => v.get.toString
        case None => ""
      }
    }, fieldDisplayName)
  }

  def getReportingFieldsFromSpec[T <: StorableClass](spec: String): List[ReportingField[T]] = ??? /*{
    val FIELD_NAME_SEPARATOR: Char = ','
    val fieldNames: List[String] = spec.split(FIELD_NAME_SEPARATOR).toList

  }*/
}