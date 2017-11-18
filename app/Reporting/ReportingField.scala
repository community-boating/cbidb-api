package Reporting

import Storable.Fields.FieldValue.{IntFieldValue, NullableIntFieldValue, NullableStringFieldValue, StringFieldValue}
import Storable.Fields._
import Storable.StorableClass

class ReportingField[T <: StorableClass](
  val valueFunction: T => String,
  val fieldDisplayName: String,
  val isDefault: Boolean
)

object ReportingField {
  def getReportingFieldFromDatabaseField[T <: StorableClass](
    f: DatabaseField[_],
    fieldDisplayName: String,
    isDefault: Boolean
): ReportingField[T] = getReportingFieldFromDatabaseFieldParentObject[T, T](f, t => t, fieldDisplayName, isDefault)

  def getReportingFieldFromDatabaseFieldParentObject[T <: StorableClass, U <: StorableClass](
    f: DatabaseField[_],
    getParent: (T => U),
    fieldDisplayName: String,
    isDefault: Boolean
  ): ReportingField[T] = f match {
    case i: IntDatabaseField => new ReportingField[T]((t: T) => {
      getParent(t).intValueMap.get(i.getRuntimeFieldName) match {
        case Some(v: IntFieldValue) => v.get.toString
        case None => ""
      }
    }, fieldDisplayName, isDefault)
    case i: NullableIntDatabaseField => new ReportingField[T]((t: T) => {
      getParent(t).nullableIntValueMap.get(i.getRuntimeFieldName) match {
        case Some(v: NullableIntFieldValue) => v.get match {
          case Some(vi: Int) => vi.toString
          case None => ""
        }
        case None => ""
      }
    }, fieldDisplayName, isDefault)
    case i: StringDatabaseField => new ReportingField[T]((t: T) => {
      getParent(t).stringValueMap.get(i.getRuntimeFieldName) match {
        case Some(v: StringFieldValue) => v.get.toString
        case None => ""
      }
    }, fieldDisplayName, isDefault)
    case i: NullableStringDatabaseField => new ReportingField[T]((t: T) => {
      getParent(t).nullableStringValueMap.get(i.getRuntimeFieldName) match {
        case Some(v: NullableStringFieldValue) => v.get.toString
        case None => ""
      }
    }, fieldDisplayName, isDefault)
  }
}